package com.segar.backend.gestionUsuarios.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componente temporal para encontrar el UUID del cliente segar-backend
 * Este componente se ejecuta al iniciar la aplicación e imprime el UUID
 */
@Component
public class ClientUUIDFinder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ClientUUIDFinder.class);

    private final Keycloak keycloak;
    private final String realm;

    public ClientUUIDFinder(Keycloak keycloak,
                            @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    @Override
    public void run(String... args) {
        logger.info("🔍 ========== BUSCANDO UUID DEL CLIENTE segar-backend ==========");

        try {
            // Intentar obtener el token de administrador primero
            String token = keycloak.tokenManager().getAccessTokenString();
            logger.info("✅ Token de administrador obtenido exitosamente");

            // Ahora listar TODOS los clientes con sus UUIDs
            logger.info("📋 Listando TODOS los clientes con sus UUIDs:");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            List<ClientRepresentation> allClients = keycloak.realm(realm).clients().findAll();

            String segarBackendUUID = null;

            for (ClientRepresentation client : allClients) {
                String clientId = client.getClientId();
                String uuid = client.getId();

                logger.info("📌 Client ID: '{}' → UUID: '{}'", clientId, uuid);

                if ("segar-backend".equals(clientId)) {
                    segarBackendUUID = uuid;
                    logger.info("   ⭐ ¡ESTE ES EL CLIENTE segar-backend!");
                }
            }

            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            if (segarBackendUUID != null) {
                logger.info("✅ UUID DEL CLIENTE segar-backend ENCONTRADO:");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                logger.info("🎯 UUID: {}", segarBackendUUID);
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                logger.info("");
                logger.info("📝 COPIA ESTA LÍNEA y agrégala a src/main/resources/application.properties:");
                logger.info("");
                logger.info("keycloak.client.segar-backend.uuid={}", segarBackendUUID);
                logger.info("");
                logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            } else {
                logger.error("❌ Cliente 'segar-backend' NO encontrado en la lista de {} clientes", allClients.size());
                logger.error("❌ Verifica que el cliente exista en Keycloak Admin Console");
            }

        } catch (Exception e) {
            logger.error("❌ Error al buscar UUID del cliente: {}", e.getMessage());
            logger.error("❌ Esto puede deberse a falta de permisos del Service Account");
            logger.info("");
            logger.info("💡 SOLUCIÓN ALTERNATIVA - Obtener UUID manualmente:");
            logger.info("1. Abre: http://localhost:8080/admin/master/console/");
            logger.info("2. Ve a: Realm 'segar' > Clients > segar-backend");
            logger.info("3. Copia el UUID de la URL del navegador");
            logger.info("4. Agrégalo en application.properties:");
            logger.info("   keycloak.client.segar-backend.uuid=<UUID_COPIADO>");
        }

        logger.info("🔍 ========== FIN DE LA BÚSQUEDA ==========");
    }
}

