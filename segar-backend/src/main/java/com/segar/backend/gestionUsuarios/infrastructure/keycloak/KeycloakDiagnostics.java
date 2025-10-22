package com.segar.backend.gestionUsuarios.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Diagnóstico detallado de la conexión con Keycloak
 */
@Component
public class KeycloakDiagnostics {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakDiagnostics.class);

    private final Keycloak keycloak;
    private final String realm;
    private final String authServerUrl;
    private final String clientId;

    public KeycloakDiagnostics(Keycloak keycloak,
                               @Value("${keycloak.realm}") String realm,
                               @Value("${keycloak.auth-server-url}") String authServerUrl,
                               @Value("${keycloak.admin.client-id}") String clientId) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void diagnoseKeycloakConnection() {
        logger.info("🔍 ========== DIAGNÓSTICO DE KEYCLOAK ==========");
        logger.info("🔍 Auth Server URL: {}", authServerUrl);
        logger.info("🔍 Realm: {}", realm);
        logger.info("🔍 Client ID: {}", clientId);
        logger.info("🔍 ===============================================");

        try {
            // Test 1: Verificar conexión básica
            logger.info("📡 Test 1: Verificando conexión con Keycloak...");
            keycloak.serverInfo().getInfo();
            logger.info("✅ Conexión exitosa con Keycloak");

            // Test 2: Verificar acceso al realm
            logger.info("📡 Test 2: Verificando acceso al realm '{}'...", realm);
            var realmInfo = keycloak.realm(realm).toRepresentation();
            logger.info("✅ Acceso exitoso al realm: {}", realmInfo.getRealm());

            // Test 3: Intentar listar clientes (puede fallar por permisos)
            logger.info("📡 Test 3: Verificando permisos para listar clientes...");
            try {
                List<ClientRepresentation> allClients = keycloak.realm(realm).clients().findAll();
                logger.info("✅ Permisos OK - Total de clientes: {}", allClients.size());

                // Buscar segar-backend en la lista
                boolean found = false;
                for (ClientRepresentation client : allClients) {
                    if ("segar-backend".equals(client.getClientId())) {
                        found = true;
                        logger.info("✅ Cliente 'segar-backend' encontrado (UUID: {})", client.getId());
                        break;
                    }
                }

                if (!found) {
                    logger.warn("⚠️ Cliente 'segar-backend' no aparece en la lista de clientes");
                }

            } catch (Exception e) {
                logger.warn("⚠️ No se pueden listar clientes (permisos limitados)");
                logger.warn("⚠️ Esto es normal si el Service Account no tiene rol 'view-clients'");
                logger.info("💡 La aplicación funcionará correctamente para gestión de usuarios");
            }

            logger.info("🔍 ========== FIN DEL DIAGNÓSTICO ==========");

        } catch (Exception e) {
            logger.error("❌ Error en diagnóstico de Keycloak: {}", e.getMessage());
            logger.warn("⚠️ Revisa la configuración de Keycloak en application.properties");
        }
    }
}
