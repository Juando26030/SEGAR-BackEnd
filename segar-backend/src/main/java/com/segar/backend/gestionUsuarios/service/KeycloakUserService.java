package com.segar.backend.gestionUsuarios.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
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
    private final String segarBackendClientUUID;

    public KeycloakUserService(Keycloak keycloak,
                               @Value("${keycloak.realm}") String realm,
                               @Value("${keycloak.client.segar-backend.uuid:}") String segarBackendClientUUID) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.segarBackendClientUUID = segarBackendClientUUID;

        if (segarBackendClientUUID == null || segarBackendClientUUID.trim().isEmpty() || "PENDIENTE_CONFIGURAR".equals(segarBackendClientUUID)) {
            logger.warn("⚠️ UUID del cliente segar-backend NO configurado en application.properties");
            logger.warn("⚠️ La asignación de roles puede fallar. Configure: keycloak.client.segar-backend.uuid");
        }
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
                assignAdminRoles(userId);
            } else if ("empleado".equalsIgnoreCase(role) || "Empleado".equalsIgnoreCase(role)) {
                logger.info("🔐 Detectado rol de EMPLEADO");
                assignEmpleadoRole(userId);
            } else {
                logger.warn("⚠️ Rol '{}' no reconocido. Roles válidos: admin, Administrador, empleado, Empleado", role);
            }

            logger.info("✅ Proceso de asignación de roles completado");

        } catch (Exception e) {
            logger.error("❌ Error general al asignar roles: {}", e.getMessage(), e);
            logger.warn("⚠️ El usuario se creó pero sin roles. Debe asignarlos manualmente en Keycloak.");
        }
    }

    private void assignAdminRoles(String userId) {
        // 1. Rol realm-admin del cliente realm-management
        try {
            logger.info("🔍 Asignando rol realm-admin...");
            String realmManagementClientId = findClientUUID("realm-management");

            if (realmManagementClientId != null) {
                RoleRepresentation realmAdminRole = keycloak.realm(realm)
                        .clients()
                        .get(realmManagementClientId)
                        .roles()
                        .get("realm-admin")
                        .toRepresentation();

                getUsersResource().get(userId)
                        .roles()
                        .clientLevel(realmManagementClientId)
                        .add(Collections.singletonList(realmAdminRole));

                logger.info("✅ Rol realm-admin asignado exitosamente");
            }
        } catch (Exception e) {
            logger.error("❌ Error al asignar realm-admin: {}", e.getMessage());
        }

        // 2. Rol admin del cliente segar-backend
        assignClientRole(userId, "admin", "Administrador del sistema");
    }

    private void assignEmpleadoRole(String userId) {
        assignClientRole(userId, "Empleado", "Empleado operativo");
    }

    private void assignClientRole(String userId, String roleName, String roleDescription) {
        try {
            String clientUUID = getSegarBackendClientUUID();

            if (clientUUID == null) {
                logger.error("❌ No se pudo obtener el UUID del cliente segar-backend");
                return;
            }

            logger.info("✅ Usando UUID del cliente: {}", clientUUID);

            // Intentar obtener el rol existente
            try {
                logger.info("🔍 Buscando rol '{}'...", roleName);
                RoleRepresentation role = keycloak.realm(realm)
                        .clients()
                        .get(clientUUID)
                        .roles()
                        .get(roleName)
                        .toRepresentation();

                logger.info("✅ Rol '{}' encontrado", roleName);

                // Asignar el rol al usuario
                logger.info("🔄 Asignando rol '{}' al usuario...", roleName);
                getUsersResource().get(userId)
                        .roles()
                        .clientLevel(clientUUID)
                        .add(Collections.singletonList(role));

                logger.info("✅ Rol '{}' asignado exitosamente", roleName);

            } catch (NotFoundException e) {
                logger.error("❌ Rol '{}' NO encontrado en el cliente segar-backend", roleName);
                logger.info("💡 Intentando crear el rol automáticamente...");

                // Intentar crear el rol
                try {
                    RoleRepresentation newRole = new RoleRepresentation();
                    newRole.setName(roleName);
                    newRole.setDescription(roleDescription);

                    keycloak.realm(realm)
                            .clients()
                            .get(clientUUID)
                            .roles()
                            .create(newRole);

                    logger.info("✅ Rol '{}' creado exitosamente", roleName);

                    // Ahora sí asignar el rol recién creado
                    RoleRepresentation createdRole = keycloak.realm(realm)
                            .clients()
                            .get(clientUUID)
                            .roles()
                            .get(roleName)
                            .toRepresentation();

                    getUsersResource().get(userId)
                            .roles()
                            .clientLevel(clientUUID)
                            .add(Collections.singletonList(createdRole));

                    logger.info("✅ Rol '{}' asignado al usuario después de crearlo", roleName);

                } catch (Exception createEx) {
                    logger.error("❌ No se pudo crear el rol '{}': {}", roleName, createEx.getMessage());
                    logger.info("💡 Crea el rol manualmente en: Keycloak > Clients > segar-backend > Roles");
                }
            }

        } catch (Exception e) {
            logger.error("❌ Error al asignar rol '{}': {}", roleName, e.getMessage());
        }
    }

    /**
     * Obtiene el UUID del cliente segar-backend
     * Primero intenta usar el UUID configurado, si no está disponible intenta buscarlo
     */
    private String getSegarBackendClientUUID() {
        // Opción 1: Usar el UUID configurado (preferido)
        if (segarBackendClientUUID != null &&
            !segarBackendClientUUID.trim().isEmpty() &&
            !"PENDIENTE_CONFIGURAR".equals(segarBackendClientUUID)) {
            logger.info("✅ Usando UUID configurado del cliente segar-backend");
            return segarBackendClientUUID;
        }

        // Opción 2: Intentar buscarlo (puede fallar por permisos)
        logger.info("⚠️ UUID no configurado, intentando buscar el cliente...");
        return findClientUUID("segar-backend");
    }

    /**
     * Busca el UUID de un cliente por su clientId
     * Puede fallar si el Service Account no tiene permisos
     */
    private String findClientUUID(String clientId) {
        try {
            // Primero intentar listar TODOS los clientes y mostrar la info completa
            logger.info("🔍 ========== LISTANDO TODOS LOS CLIENTES ==========");
            List<ClientRepresentation> allClients = keycloak.realm(realm).clients().findAll();
            logger.info("📋 Total de clientes encontrados: {}", allClients.size());

            String foundUUID = null;

            for (ClientRepresentation client : allClients) {
                String cId = client.getClientId();
                String uuid = client.getId();
                logger.info("📌 Client ID: '{}' → UUID: '{}'", cId, uuid);

                if (clientId.equals(cId)) {
                    foundUUID = uuid;
                    logger.info("   ⭐⭐⭐ ¡ESTE ES EL CLIENTE QUE BUSCAS! ⭐⭐⭐");
                    logger.info("   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    logger.info("   🎯 UUID: {}", uuid);
                    logger.info("   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    logger.info("");
                    logger.info("   📝 COPIA Y PEGA ESTA LÍNEA en application.properties:");
                    logger.info("");
                    logger.info("   keycloak.client.{}.uuid={}", clientId, uuid);
                    logger.info("");
                    logger.info("   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                }
            }

            logger.info("🔍 ========== FIN DEL LISTADO ==========");

            if (foundUUID != null) {
                return foundUUID;
            }

            // Si llegamos aquí, intentar el método normal
            var clients = keycloak.realm(realm)
                    .clients()
                    .findByClientId(clientId);

            if (!clients.isEmpty()) {
                String uuid = clients.get(0).getId();
                logger.info("✅ Cliente '{}' encontrado con UUID: {}", clientId, uuid);
                return uuid;
            } else {
                logger.error("❌ Cliente '{}' NO encontrado", clientId);
                return null;
            }
        } catch (Exception e) {
            logger.error("❌ Error al buscar cliente '{}': {}", clientId, e.getMessage());
            logger.info("💡 Configure el UUID manualmente en application.properties:");
            logger.info("   keycloak.client.segar-backend.uuid=<UUID_DEL_CLIENTE>");
            return null;
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
