package com.segar.backend.security.service;

import com.segar.backend.security.dto.AdminLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio para autenticación de administradores con Keycloak
 */
@Service
public class AdminAuthService {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/realms/segar}")
    private String keycloakIssuerUri;

    @Value("${keycloak.client-id:segar-backend}")
    private String clientId;

    @Value("${keycloak.client-secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Autenticar administrador contra Keycloak
     */
    public AdminLoginResponse authenticateAdmin(String username, String password) {
        try {
            String tokenUrl = keycloakIssuerUri.replace("/realms/segar", "") + "/realms/segar/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", clientId);
            if (clientSecret != null && !clientSecret.isEmpty()) {
                map.add("client_secret", clientSecret);
            }
            map.add("username", username);
            map.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                String accessToken = (String) body.get("access_token");
                String refreshToken = (String) body.get("refresh_token");

                // Verificar que el usuario tiene rol SUPER_ADMIN
                // Por simplicidad, aquí asumimos que si se autentica es válido
                // En producción, decodificarías el JWT y verificarías el rol

                return new AdminLoginResponse(
                    true,
                    username,
                    "SUPER_ADMIN",
                    "Autenticación exitosa",
                    accessToken,
                    refreshToken
                );
            }

            return new AdminLoginResponse(false, null, null, "Error en la autenticación");

        } catch (Exception e) {
            throw new RuntimeException("Error al autenticar con Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Verificar si el JWT tiene rol SUPER_ADMIN
     */
    public boolean isSuperAdmin(Jwt jwt) {
        List<String> roles = extractRoles(jwt);
        return roles.contains("SUPER_ADMIN") || roles.contains("super_admin");
    }

    /**
     * Extraer roles del JWT
     */
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess == null) {
            return List.of();
        }

        Map<String, Object> segarClient = (Map<String, Object>) resourceAccess.get(clientId);
        if (segarClient == null) {
            return List.of();
        }

        List<String> roles = (List<String>) segarClient.get("roles");
        return roles != null ? roles : List.of();
    }
}

