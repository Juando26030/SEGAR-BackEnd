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

            // Test 3: Listar TODOS los clientes disponibles
            logger.info("📡 Test 3: Listando TODOS los clientes en el realm...");
            List<ClientRepresentation> allClients = keycloak.realm(realm).clients().findAll();
            logger.info("📋 Total de clientes encontrados: {}", allClients.size());

            for (ClientRepresentation client : allClients) {
                logger.info("   📌 Cliente ID: '{}' (UUID: {})", client.getClientId(), client.getId());
                logger.info("      - Enabled: {}", client.isEnabled());
                logger.info("      - Service Accounts Enabled: {}", client.isServiceAccountsEnabled());
            }

            // Test 4: Buscar específicamente el cliente segar-backend
            logger.info("📡 Test 4: Buscando cliente específico 'segar-backend'...");
            List<ClientRepresentation> segarClients = keycloak.realm(realm)
                    .clients()
                    .findByClientId("segar-backend");

            if (segarClients.isEmpty()) {
                logger.error("❌ Cliente 'segar-backend' NO encontrado usando findByClientId()");
                logger.error("❌ Esto es RARO porque sí aparece en la lista de todos los clientes");

                // Buscar manualmente en la lista
                logger.info("🔍 Buscando manualmente en la lista completa...");
                boolean found = false;
                for (ClientRepresentation client : allClients) {
                    if ("segar-backend".equals(client.getClientId())) {
                        found = true;
                        logger.info("✅ ¡ENCONTRADO MANUALMENTE!");
                        logger.info("   - UUID: {}", client.getId());
                        logger.info("   - Enabled: {}", client.isEnabled());
                        logger.info("   - Service Accounts: {}", client.isServiceAccountsEnabled());
                        logger.info("   - Public Client: {}", client.isPublicClient());
                        break;
                    }
                }

                if (!found) {
                    logger.error("❌ No se encontró 'segar-backend' ni siquiera manualmente");
                }

            } else {
                ClientRepresentation segarClient = segarClients.getFirst();
                logger.info("✅ Cliente 'segar-backend' encontrado con UUID: {}", segarClient.getId());
                logger.info("   - Enabled: {}", segarClient.isEnabled());
                logger.info("   - Service Accounts Enabled: {}", segarClient.isServiceAccountsEnabled());
                logger.info("   - Public Client: {}", segarClient.isPublicClient());
                logger.info("   - Protocol: {}", segarClient.getProtocol());

                // Test 5: Verificar roles del cliente
                logger.info("📡 Test 5: Verificando roles del cliente...");
                try {
                    var roles = keycloak.realm(realm)
                            .clients()
                            .get(segarClient.getId())
                            .roles()
                            .list();
                    logger.info("✅ Roles disponibles en 'segar-backend': {}", roles.size());
                    roles.forEach(role -> logger.info("   - Rol: '{}'", role.getName()));
                } catch (Exception e) {
                    logger.error("❌ Error al obtener roles: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("❌ Error durante el diagnóstico: {}", e.getMessage(), e);
            logger.error("❌ Causa raíz: {}", e.getCause() != null ? e.getCause().getMessage() : "N/A");
        }

        logger.info("🔍 ========== FIN DEL DIAGNÓSTICO ==========");
    }
}

