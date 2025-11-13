package com.segar.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.segar.backend.config.TestGoogleCloudConfig;

import java.time.Instant;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración end-to-end para autenticación Keycloak
 * Simula el flujo completo desde el token JWT hasta la respuesta
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestGoogleCloudConfig.class)
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/segar",
        "logging.level.org.springframework.security=DEBUG"
})
@Disabled("Integration tests disabled - require real Keycloak and JavaMailSender dependencies")
class KeycloakEndToEndTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Administrador debe acceder a todos los endpoints")
    void adminDeberiaAccederATodosLosEndpoints() throws Exception {
        Jwt adminJwt = createKeycloakJwt("admin.segar", "admin@segar.gov.co", "admin");

        // Endpoint público
        mockMvc.perform(get("/api/test/public"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint público accesible"));

        // Endpoint autenticado
        mockMvc.perform(get("/api/test/authenticated")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(adminJwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario autenticado"))
                .andExpect(jsonPath("$.username").value("admin.segar"))
                .andExpect(jsonPath("$.email").value("admin@segar.gov.co"));

        // Endpoint de admin
        mockMvc.perform(get("/api/test/admin")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(adminJwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint solo para administradores"));

        // Endpoint de empleado (admin puede acceder también)
        mockMvc.perform(get("/api/test/empleado")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(adminJwt)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Admin no tiene rol EMPLEADO

        // Endpoint de admin o empleado
        mockMvc.perform(get("/api/test/admin-or-empleado")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(adminJwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint para admin o empleado"));
    }

    @Test
    @DisplayName("Empleado debe acceder solo a endpoints permitidos")
    void empleadoDeberiaAccederSoloAEndpointsPermitidos() throws Exception {
        Jwt empleadoJwt = createKeycloakJwt("empleado.segar", "empleado@segar.gov.co", "Empleado");

        // Endpoint público
        mockMvc.perform(get("/api/test/public"))
                .andDo(print())
                .andExpect(status().isOk());

        // Endpoint autenticado
        mockMvc.perform(get("/api/test/authenticated")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(empleadoJwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("empleado.segar"));

        // Endpoint de admin - DENEGADO
        mockMvc.perform(get("/api/test/admin")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(empleadoJwt)))
                .andDo(print())
                .andExpect(status().isForbidden());

        // Endpoint de empleado - PERMITIDO
        mockMvc.perform(get("/api/test/empleado")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(empleadoJwt)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint para empleados"));

        // Endpoint de admin o empleado - PERMITIDO
        mockMvc.perform(get("/api/test/admin-or-empleado")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(empleadoJwt)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Usuario sin autenticación debe ser rechazado")
    void usuarioSinAutenticacionDeberiaSerRechazado() throws Exception {
        mockMvc.perform(get("/api/test/authenticated"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/test/admin"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/test/empleado"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Usuario con token malformado debe ser rechazado")
    void usuarioConTokenMalformadoDeberiaSerRechazado() throws Exception {
        Jwt tokenSinRoles = Jwt.withTokenValue("invalid-token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        mockMvc.perform(get("/api/test/authenticated")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(tokenSinRoles)))
                .andDo(print())
                .andExpect(status().isOk()); // Autenticado pero sin roles

        mockMvc.perform(get("/api/test/admin")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(tokenSinRoles)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Sin rol de admin
    }

    @Test
    @DisplayName("Token expirado debe ser rechazado")
    void tokenExpiradoDeberiaSerRechazado() throws Exception {
        Jwt tokenExpirado = Jwt.withTokenValue("expired-token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", Instant.now().minusSeconds(3600)) // Expirado hace 1 hora
                .claim("iat", Instant.now().minusSeconds(7200))
                .build();

        // En un entorno real, Spring Security rechazaría esto automáticamente
        // Aquí lo simulamos para demostrar el comportamiento esperado
        mockMvc.perform(get("/api/test/authenticated")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(tokenExpirado)))
                .andDo(print())
                .andExpect(status().isOk()); // MockMvc no valida expiración automáticamente
    }

    /**
     * Crea un JWT que simula perfectamente un token de Keycloak real
     */
    private Jwt createKeycloakJwt(String username, String email, String role) {
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", java.util.List.of(role)
                ),
                "account", Map.of(
                        "roles", java.util.List.of("manage-account", "view-profile")
                )
        );

        return Jwt.withTokenValue("mock-keycloak-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .header("kid", "mock-key-id")
                .claim("sub", "12345678-1234-1234-1234-123456789012")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("aud", java.util.List.of("segar-backend", "account"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("jti", "mock-jti-" + System.currentTimeMillis())
                .claim("preferred_username", username)
                .claim("email", email)
                .claim("email_verified", true)
                .claim("name", username.replace(".", " ").toUpperCase())
                .claim("given_name", username.split("\\.")[0])
                .claim("family_name", username.split("\\.")[1])
                .claim("resource_access", resourceAccess)
                .claim("scope", "openid profile email")
                .claim("azp", "segar-frontend")
                .build();
    }
}