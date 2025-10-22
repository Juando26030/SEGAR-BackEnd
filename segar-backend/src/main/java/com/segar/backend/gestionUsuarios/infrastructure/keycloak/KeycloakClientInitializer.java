package com.segar.backend.gestionUsuarios.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inicializador que verifica y crea el cliente segar-backend y sus roles en Keycloak
 */
@Component
public class KeycloakClientInitializer {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClientInitializer.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakClientInitializer(Keycloak keycloak,
                                     @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void initializeKeycloakClient() {
        try {
            logger.info("🔧 ========== INICIALIZANDO ROLES DEL CLIENTE KEYCLOAK ==========");
            logger.info("🔧 Realm: {}", realm);

            String clientUUID = null;

            // Intentar buscar el cliente por clientId
            try {
                List<ClientRepresentation> clients = keycloak.realm(realm)
                        .clients()
                        .findByClientId("segar-backend");

                if (!clients.isEmpty()) {
                    clientUUID = clients.get(0).getId();
                    logger.info("✅ Cliente 'segar-backend' encontrado con UUID: {}", clientUUID);
                }
            } catch (Exception e) {
                logger.warn("⚠️ No se pudo buscar el cliente por clientId (posible falta de permisos): {}", e.getMessage());
            }

            // Si no se encontró por búsqueda, intentar listar todos y buscar manualmente
            if (clientUUID == null) {
                try {
                    logger.info("🔍 Intentando listar todos los clientes...");
                    List<ClientRepresentation> allClients = keycloak.realm(realm).clients().findAll();

                    for (ClientRepresentation client : allClients) {
                        if ("segar-backend".equals(client.getClientId())) {
                            clientUUID = client.getId();
                            logger.info("✅ Cliente 'segar-backend' encontrado en lista completa con UUID: {}", clientUUID);
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.warn("⚠️ No se pudo listar todos los clientes: {}", e.getMessage());
                }
            }

            if (clientUUID == null) {
                logger.warn("⚠️ No se pudo obtener el UUID del cliente 'segar-backend'");
                logger.warn("⚠️ Esto puede deberse a falta de permisos del Service Account");
                logger.warn("⚠️ La aplicación seguirá funcionando, pero no se verificarán los roles");
                logger.info("💡 Para habilitar la gestión automática de roles:");
                logger.info("   1. En Keycloak Admin Console, ve a Clients > segar-backend");
                logger.info("   2. En la pestaña 'Service account roles', asigna los roles:");
                logger.info("      - realm-management > view-clients");
                logger.info("      - realm-management > manage-clients");
                return;
            }

            // Verificar y crear roles
            ensureRoleExists(clientUUID, "Empleado", "Empleado operativo");
            ensureRoleExists(clientUUID, "admin", "Administrador del sistema");

            logger.info("✅ ========== INICIALIZACIÓN COMPLETADA ==========");

        } catch (Exception e) {
            logger.error("❌ Error al inicializar cliente Keycloak: {}", e.getMessage());
            logger.warn("⚠️ La aplicación continuará, pero algunos roles pueden no estar configurados");
        }
    }

    private void ensureRoleExists(String clientId, String roleName, String roleDescription) {
        try {
            // Intentar obtener el rol para verificar si existe
            keycloak.realm(realm)
                    .clients()
                    .get(clientId)
                    .roles()
                    .get(roleName)
                    .toRepresentation();

            logger.info("✅ Rol '{}' ya existe en el cliente", roleName);

        } catch (Exception e) {
            // Si no existe, intentar crearlo
            try {
                logger.info("🔧 Creando rol '{}' en cliente segar-backend...", roleName);

                RoleRepresentation newRole = new RoleRepresentation();
                newRole.setName(roleName);
                newRole.setDescription(roleDescription);

                keycloak.realm(realm)
                        .clients()
                        .get(clientId)
                        .roles()
                        .create(newRole);

                logger.info("✅ Rol '{}' creado exitosamente", roleName);

            } catch (Exception createException) {
                logger.warn("⚠️ No se pudo crear el rol '{}': {}", roleName, createException.getMessage());
                logger.info("💡 Por favor, crea el rol manualmente en Keycloak Admin Console");
            }
        }
    }
}
