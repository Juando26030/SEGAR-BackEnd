package com.segar.backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la integración con Keycloak
 * Valida la extracción de roles y claims del JWT
 */
class JwtKeycloakUnitTest {

    @Test
    @DisplayName("Debería extraer roles correctamente de un JWT de Keycloak")
    void deberiaExtraerRolesCorrectamenteDeJwtKeycloak() {
        // Given - Crear un JWT que simula la estructura de Keycloak
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of("admin", "empleado")
                ),
                "account", Map.of(
                        "roles", List.of("manage-account", "view-profile")
                )
        );

        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("sub", "test-user-id")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("preferred_username", "admin.segar")
                .claim("email", "admin@segar.gov.co")
                .claim("resource_access", resourceAccess)
                .build();

        // When - Extraer información del JWT
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        Map<String, Object> resourceAccessClaim = jwt.getClaimAsMap("resource_access");
        
        // Then - Verificar que la información se extrajo correctamente
        assertNotNull(jwt);
        assertEquals("admin.segar", username);
        assertEquals("admin@segar.gov.co", email);
        assertNotNull(resourceAccessClaim);
        
        // Verificar roles específicos
        @SuppressWarnings("unchecked")
        Map<String, Object> segarBackendRoles = (Map<String, Object>) resourceAccessClaim.get("segar-backend");
        assertNotNull(segarBackendRoles);
        
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) segarBackendRoles.get("roles");
        assertNotNull(roles);
        assertTrue(roles.contains("admin"));
        assertTrue(roles.contains("empleado"));
        assertEquals(2, roles.size());
    }

    @Test
    @DisplayName("Debería manejar JWT sin roles correctamente")
    void deberiaManejarJwtSinRolesCorrectamente() {
        // Given - JWT sin resource_access
        Jwt jwt = Jwt.withTokenValue("mock-token-no-roles")
                .header("alg", "RS256")
                .claim("sub", "test-user-no-roles")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("preferred_username", "usuario.sin.roles")
                .claim("email", "usuario@example.com")
                .build();

        // When - Intentar extraer roles
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        // Then - Verificar manejo correcto de ausencia de roles
        assertNotNull(jwt);
        assertEquals("usuario.sin.roles", username);
        assertEquals("usuario@example.com", email);
        assertNull(resourceAccess); // No debe tener resource_access
    }

    @Test
    @DisplayName("Debería validar token expirado")
    void deberiaValidarTokenExpirado() {
        // Given - JWT con tiempo de expiración pasado
        Instant pastTime = Instant.now().minusSeconds(3600); // Expirado hace 1 hora
        
        Jwt jwt = Jwt.withTokenValue("expired-token")
                .header("alg", "RS256")
                .claim("sub", "expired-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", pastTime)
                .claim("iat", pastTime.minusSeconds(3600))
                .claim("preferred_username", "usuario.expirado")
                .build();

        // When & Then - Verificar que el token se puede crear pero está expirado
        assertNotNull(jwt);
        assertEquals("usuario.expirado", jwt.getClaimAsString("preferred_username"));
        
        // Verificar que la fecha de expiración es en el pasado
        Instant expiration = jwt.getExpiresAt();
        assertNotNull(expiration);
        assertTrue(expiration.isBefore(Instant.now()), 
                "El token debería estar expirado");
    }

    @Test
    @DisplayName("Debería extraer claims específicos de Keycloak")
    void deberiaExtraerClaimsEspecificosDeKeycloak() {
        // Given - JWT con claims específicos de Keycloak
        Jwt jwt = Jwt.withTokenValue("keycloak-claims-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("sub", "12345678-1234-1234-1234-123456789012")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("aud", List.of("segar-backend", "account"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("jti", "unique-jwt-id")
                .claim("preferred_username", "juan.perez")
                .claim("email", "juan.perez@segar.gov.co")
                .claim("email_verified", true)
                .claim("name", "Juan Pérez")
                .claim("given_name", "Juan")
                .claim("family_name", "Pérez")
                .claim("scope", "openid profile email")
                .claim("azp", "segar-frontend")
                .build();

        // When - Extraer claims específicos
        String subject = jwt.getSubject();
        String issuer = jwt.getIssuer().toString();
        @SuppressWarnings("unchecked")
        List<String> audience = (List<String>) jwt.getClaim("aud");
        String name = jwt.getClaimAsString("name");
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        String scope = jwt.getClaimAsString("scope");

        // Then - Verificar claims
        assertEquals("12345678-1234-1234-1234-123456789012", subject);
        assertEquals("http://localhost:8080/realms/segar", issuer);
        assertTrue(audience.contains("segar-backend"));
        assertTrue(audience.contains("account"));
        assertEquals("Juan Pérez", name);
        assertTrue(emailVerified);
        assertEquals("openid profile email", scope);
    }

    @Test
    @DisplayName("Debería simular estructura completa de Keycloak JWT")
    void deberiaSimularEstructuraCompletaKeycloakJwt() {
        // Given - JWT completo como lo devuelve Keycloak
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of("ADMIN")
                ),
                "account", Map.of(
                        "roles", List.of("manage-account", "manage-account-links", "view-profile")
                )
        );

        Jwt jwt = Jwt.withTokenValue("complete-keycloak-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .header("kid", "keycloak-key-id")
                .claim("sub", "f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("aud", List.of("segar-backend", "account"))
                .claim("exp", Instant.now().plusSeconds(300)) // 5 minutos
                .claim("iat", Instant.now())
                .claim("auth_time", Instant.now().minusSeconds(60))
                .claim("jti", "jwt-id-" + System.currentTimeMillis())
                .claim("typ", "Bearer")
                .claim("azp", "segar-frontend")
                .claim("session_state", "session-" + System.currentTimeMillis())
                .claim("acr", "1")
                .claim("allowed-origins", List.of("http://localhost:4200"))
                .claim("realm_access", Map.of("roles", List.of("offline_access", "uma_authorization")))
                .claim("resource_access", resourceAccess)
                .claim("scope", "openid profile email")
                .claim("sid", "session-id")
                .claim("email_verified", true)
                .claim("name", "Administrador SEGAR")
                .claim("preferred_username", "admin.segar")
                .claim("given_name", "Administrador")
                .claim("family_name", "SEGAR")
                .claim("email", "admin@segar.gov.co")
                .build();

        // When & Then - Verificar todos los elementos del JWT
        assertAll("Validación completa del JWT de Keycloak",
                () -> assertNotNull(jwt.getTokenValue()),
                () -> assertEquals("RS256", jwt.getHeaders().get("alg")),
                () -> assertEquals("JWT", jwt.getHeaders().get("typ")),
                () -> assertNotNull(jwt.getSubject()),
                () -> assertTrue(jwt.getExpiresAt().isAfter(Instant.now())),
                () -> assertEquals("admin.segar", jwt.getClaimAsString("preferred_username")),
                () -> assertEquals("admin@segar.gov.co", jwt.getClaimAsString("email")),
                () -> assertTrue(jwt.getClaimAsBoolean("email_verified")),
                () -> assertEquals("openid profile email", jwt.getClaimAsString("scope")),
                () -> assertNotNull(jwt.getClaimAsMap("resource_access"))
        );

        // Verificar roles específicos del backend SEGAR
        Map<String, Object> resourceAccessClaim = jwt.getClaimAsMap("resource_access");
        @SuppressWarnings("unchecked")
        Map<String, Object> segarBackend = (Map<String, Object>) resourceAccessClaim.get("segar-backend");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) segarBackend.get("roles");
        
        assertTrue(roles.contains("ADMIN"), "Debe contener el rol ADMIN");
        
        System.out.println("✅ JWT de Keycloak validado exitosamente");
        System.out.println("📋 Usuario: " + jwt.getClaimAsString("preferred_username"));
        System.out.println("📧 Email: " + jwt.getClaimAsString("email"));
        System.out.println("🔐 Roles: " + roles);
        System.out.println("⏰ Expira en: " + jwt.getExpiresAt());
    }
}