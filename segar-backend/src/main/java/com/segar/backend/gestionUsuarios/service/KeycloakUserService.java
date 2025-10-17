package com.segar.backend.gestionUsuarios.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserService(Keycloak keycloak,
                               @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String createUser(String username, String email, String password,
                             String firstName, String lastName, String role) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // ✅ Importante: Eliminar todas las acciones requeridas para que pueda hacer login inmediatamente
        user.setRequiredActions(Collections.emptyList());

        Response response = getUsersResource().create(user);

        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath()
                    .replaceAll(".*/([^/]+)$", "$1");

            // Establecer contraseña (NO temporal)
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            getUsersResource().get(userId).resetPassword(credential);

            // ✅ Asignar roles según la categoría del usuario
            assignRolesToUser(userId, role);

            logger.info("✅ Usuario creado y listo para login: {} (ID: {}) con rol: {}", username, userId, role);

            return userId;
        }

        throw new RuntimeException("Error al crear usuario: " +
                response.getStatusInfo());
    }

    /**
     * Asigna roles al usuario según su categoría
     * - admin: realm-admin + segar-backend/admin
     * - empleado: segar-backend/Empleado
     */
    private void assignRolesToUser(String userId, String role) {
        try {
            logger.info("🔐 ========== ASIGNANDO ROLES ==========");
            logger.info("🔐 Usuario ID: {}", userId);
            logger.info("🔐 Rol a asignar: '{}'", role);
            logger.info("🔐 =====================================");

            if (role == null || role.trim().isEmpty()) {
                logger.error("❌ El rol está NULL o VACÍO. No se pueden asignar roles.");
                return;
            }

            if ("admin".equalsIgnoreCase(role) || "Administrador".equalsIgnoreCase(role)) {
                logger.info("🔐 Detectado rol de ADMINISTRADOR");

                // 1. Rol realm-admin del cliente realm-management
                try {
                    logger.info("🔍 Buscando cliente realm-management...");
                    var realmManagementClients = keycloak.realm(realm)
                            .clients()
                            .findByClientId("realm-management");

                    if (realmManagementClients.isEmpty()) {
                        logger.error("❌ Cliente realm-management NO encontrado");
                    } else {
                        String realmManagementClientId = realmManagementClients.get(0).getId();
                        logger.info("✅ Cliente realm-management encontrado: {}", realmManagementClientId);

                        logger.info("🔍 Buscando rol realm-admin...");
                        RoleRepresentation realmAdminRole = keycloak.realm(realm)
                                .clients()
                                .get(realmManagementClientId)
                                .roles()
                                .get("realm-admin")
                                .toRepresentation();
                        logger.info("✅ Rol realm-admin encontrado");

                        logger.info("🔄 Asignando realm-admin al usuario...");
                        getUsersResource().get(userId)
                                .roles()
                                .clientLevel(realmManagementClientId)
                                .add(Collections.singletonList(realmAdminRole));

                        logger.info("✅ Rol realm-admin asignado exitosamente");
                    }
                } catch (Exception e) {
                    logger.error("❌ Error al asignar realm-admin: {}", e.getMessage(), e);
                }

                // 2. Rol admin del cliente segar-backend
                try {
                    logger.info("🔍 Buscando cliente segar-backend...");
                    var segarBackendClients = keycloak.realm(realm)
                            .clients()
                            .findByClientId("segar-backend");

                    if (segarBackendClients.isEmpty()) {
                        logger.error("❌ Cliente 'segar-backend' NO encontrado en Keycloak");
                        logger.info("🔍 Listando todos los clientes disponibles:");
                        keycloak.realm(realm).clients().findAll().forEach(client ->
                            logger.info("   - Cliente: {} (ID: {})", client.getClientId(), client.getId())
                        );
                    } else {
                        String segarBackendClientId = segarBackendClients.get(0).getId();
                        logger.info("✅ Cliente segar-backend encontrado: {}", segarBackendClientId);

                        logger.info("🔍 Buscando rol admin...");
                        RoleRepresentation adminRole = keycloak.realm(realm)
                                .clients()
                                .get(segarBackendClientId)
                                .roles()
                                .get("admin")
                                .toRepresentation();
                        logger.info("✅ Rol admin encontrado");

                        logger.info("🔄 Asignando admin al usuario...");
                        getUsersResource().get(userId)
                                .roles()
                                .clientLevel(segarBackendClientId)
                                .add(Collections.singletonList(adminRole));

                        logger.info("✅ Rol segar-backend/admin asignado exitosamente");
                    }
                } catch (Exception e) {
                    logger.error("❌ Error al asignar segar-backend/admin: {}", e.getMessage(), e);
                }

            } else if ("empleado".equalsIgnoreCase(role) || "Empleado".equalsIgnoreCase(role)) {
                logger.info("🔐 Detectado rol de EMPLEADO");

                // Rol Empleado del cliente segar-backend
                try {
                    logger.info("🔍 Buscando cliente segar-backend...");
                    var segarBackendClients = keycloak.realm(realm)
                            .clients()
                            .findByClientId("segar-backend");

                    if (segarBackendClients.isEmpty()) {
                        logger.error("❌ Cliente 'segar-backend' NO encontrado en Keycloak");
                        logger.info("🔍 Listando todos los clientes disponibles en el realm '{}':", realm);
                        keycloak.realm(realm).clients().findAll().forEach(client ->
                            logger.info("   - Cliente: '{}' (ID: {})", client.getClientId(), client.getId())
                        );
                        logger.error("❌ NO SE PUEDE ASIGNAR EL ROL. Debe crear el cliente 'segar-backend' en Keycloak primero.");
                    } else {
                        String segarBackendClientId = segarBackendClients.get(0).getId();
                        logger.info("✅ Cliente segar-backend encontrado: {}", segarBackendClientId);

                        logger.info("🔍 Buscando rol Empleado...");
                        try {
                            RoleRepresentation empleadoRole = keycloak.realm(realm)
                                    .clients()
                                    .get(segarBackendClientId)
                                    .roles()
                                    .get("Empleado")
                                    .toRepresentation();
                            logger.info("✅ Rol Empleado encontrado");

                            logger.info("🔄 Asignando Empleado al usuario...");
                            getUsersResource().get(userId)
                                    .roles()
                                    .clientLevel(segarBackendClientId)
                                    .add(Collections.singletonList(empleadoRole));

                            logger.info("✅ Rol segar-backend/Empleado asignado exitosamente");
                        } catch (NotFoundException e) {
                            logger.error("❌ Rol 'Empleado' NO encontrado en el cliente segar-backend");
                            logger.info("🔍 Listando todos los roles disponibles en segar-backend:");
                            keycloak.realm(realm).clients().get(segarBackendClientId).roles().list().forEach(r ->
                                logger.info("   - Rol: '{}'", r.getName())
                            );
                            logger.error("❌ Debe crear el rol 'Empleado' en el cliente 'segar-backend' en Keycloak.");
                        }
                    }
                } catch (Exception e) {
                    logger.error("❌ Error al asignar segar-backend/Empleado: {}", e.getMessage(), e);
                }
            } else {
                logger.warn("⚠️ Rol '{}' no reconocido. Roles válidos: admin, Administrador, empleado, Empleado", role);
            }

            logger.info("✅ Proceso de asignación de roles completado");

        } catch (Exception e) {
            logger.error("❌ Error general al asignar roles: {}", e.getMessage(), e);
            // NO lanzar excepción para que el usuario se cree aunque no se asignen los roles
            logger.warn("⚠️ El usuario se creó pero sin roles. Debe asignarlos manualmente en Keycloak.");
        }
    }

    public void updateUser(String userId, String email, String firstName, String lastName, Boolean enabled) {
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();

        if (email != null) {
            user.setEmail(email);
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }

        getUsersResource().get(userId).update(user);
    }

    public void updatePassword(String userId, String newPassword, boolean temporary) {
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(temporary);

            getUsersResource().get(userId).resetPassword(credential);
            logger.info("✅ Contraseña actualizada exitosamente en Keycloak para usuario: {}", userId);
        } catch (NotFoundException e) {
            logger.error("❌ Usuario no encontrado en Keycloak con ID: {}. Puede que se haya eliminado manualmente de Keycloak.", userId);
            throw new RuntimeException("El usuario no existe en Keycloak. Es posible que haya sido eliminado. " +
                    "Por favor, elimine y vuelva a crear este usuario.", e);
        } catch (ForbiddenException e) {
            logger.error("❌ Error 403 al actualizar contraseña en Keycloak para usuario: {}. Permisos insuficientes.", userId);
            throw new RuntimeException("No se tienen permisos para actualizar contraseñas en Keycloak. Contacte al administrador.", e);
        } catch (Exception e) {
            logger.error("❌ Error inesperado al actualizar contraseña en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar contraseña: " + e.getMessage(), e);
        }
    }

    public void enableUser(String userId, boolean enabled) {
        try {
            UserRepresentation user = getUsersResource().get(userId).toRepresentation();
            user.setEnabled(enabled);
            getUsersResource().get(userId).update(user);
            logger.info("✅ Estado enabled actualizado a {} en Keycloak para usuario: {}", enabled, userId);
        } catch (ForbiddenException e) {
            logger.error("❌ Error 403 al cambiar estado en Keycloak para usuario: {}. Permisos insuficientes.", userId);
            throw new RuntimeException("No se tienen permisos para cambiar estado de usuarios en Keycloak", e);
        } catch (Exception e) {
            logger.error("❌ Error inesperado al cambiar estado en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }

    public List<UserRepresentation> getAllUsers() {
        return getUsersResource().list();
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return getUsersResource().get(userId).toRepresentation();
        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al obtener usuario de Keycloak con ID: {}. Permisos insuficientes.", userId);
            throw e;
        } catch (NotFoundException e) {
            logger.warn("⚠️ Usuario no encontrado en Keycloak con ID: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error inesperado al obtener usuario de Keycloak: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<UserRepresentation> getUserByIdSafe(String userId) {
        try {
            UserRepresentation user = getUsersResource().get(userId).toRepresentation();
            return Optional.of(user);
        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al obtener usuario de Keycloak con ID: {}. Retornando empty.", userId);
            return Optional.empty();
        } catch (NotFoundException e) {
            logger.warn("⚠️ Usuario no encontrado en Keycloak con ID: {}", userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("❌ Error inesperado al obtener usuario de Keycloak: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserRepresentation> getUserByUsername(String username) {
        try {
            List<UserRepresentation> users = getUsersResource().search(username, true);

            if (users.isEmpty()) {
                logger.info("Usuario no encontrado en Keycloak: {}", username);
                return Optional.empty();
            }

            // Buscar coincidencia exacta
            Optional<UserRepresentation> exactMatch = users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();

            if (exactMatch.isPresent()) {
                return exactMatch;
            }

            // Si no hay coincidencia exacta, retornar el primero
            return Optional.of(users.get(0));

        } catch (ForbiddenException e) {
            logger.warn("⚠️ Error 403 al buscar usuario en Keycloak: {}. Permisos insuficientes.", username);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("❌ Error inesperado al buscar usuario en Keycloak: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteUser(String userId) {
        try {
            getUsersResource().delete(userId);
            logger.info("✅ Usuario eliminado exitosamente de Keycloak: {}", userId);
        } catch (ForbiddenException e) {
            logger.error("❌ Error 403 al eliminar usuario en Keycloak: {}. Permisos insuficientes.", userId);
            throw new RuntimeException("No se tienen permisos para eliminar usuarios en Keycloak", e);
        } catch (Exception e) {
            logger.error("❌ Error inesperado al eliminar usuario en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Limpia las acciones requeridas de un usuario para permitirle hacer login inmediatamente.
     * Útil para usuarios que ya existen y tienen el error "Account is not fully set up"
     */
    public void clearRequiredActions(String userId) {
        try {
            UserRepresentation user = getUsersResource().get(userId).toRepresentation();

            logger.info("🔍 Usuario {} tiene acciones requeridas: {}",
                    user.getUsername(), user.getRequiredActions());

            // Limpiar todas las acciones requeridas
            user.setRequiredActions(Collections.emptyList());

            // Asegurar que está habilitado y verificado
            user.setEnabled(true);
            user.setEmailVerified(true);

            getUsersResource().get(userId).update(user);

            logger.info("✅ Acciones requeridas eliminadas para usuario: {} (ID: {})",
                    user.getUsername(), userId);
        } catch (NotFoundException e) {
            logger.error("❌ Usuario no encontrado en Keycloak con ID: {}", userId);
            throw new RuntimeException("El usuario no existe en Keycloak.", e);
        } catch (Exception e) {
            logger.error("❌ Error al limpiar acciones requeridas: {}", e.getMessage());
            throw new RuntimeException("Error al limpiar acciones requeridas: " + e.getMessage(), e);
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }
}
