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
            logger.info("🔧 ========== INICIALIZANDO CLIENTE KEYCLOAK ==========");
            logger.info("🔧 Realm: {}", realm);

            // Verificar si el cliente segar-backend existe
            List<ClientRepresentation> clients = keycloak.realm(realm)
                    .clients()
                    .findByClientId("segar-backend");

            if (clients.isEmpty()) {
                logger.error("❌ Cliente 'segar-backend' NO encontrado en Keycloak");
                logger.error("❌ Por favor, cree el cliente 'segar-backend' en Keycloak manualmente");
                logger.info("📋 Clientes disponibles:");
                keycloak.realm(realm).clients().findAll().forEach(client ->
                    logger.info("   - {}", client.getClientId())
                );
                return;
            }

            String clientId = clients.getFirst().getId();
            logger.info("✅ Cliente 'segar-backend' encontrado con ID: {}", clientId);

            // Verificar y crear rol "Empleado" si no existe
            ensureRoleExists(clientId, "Empleado", "Empleado operativo");

            // Verificar y crear rol "admin" si no existe
            ensureRoleExists(clientId, "admin", "Administrador del sistema");

            logger.info("✅ ========== INICIALIZACIÓN COMPLETADA ==========");

        } catch (Exception e) {
            logger.error("❌ Error al inicializar cliente Keycloak: {}", e.getMessage(), e);
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

            logger.info("✅ Rol '{}' ya existe", roleName);

        } catch (Exception e) {
            // Si no existe, crearlo
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
                logger.error("❌ Error al crear rol '{}': {}", roleName, createException.getMessage());
            }
        }
    }
}
