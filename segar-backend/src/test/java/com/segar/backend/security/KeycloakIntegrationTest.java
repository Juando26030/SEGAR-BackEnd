package com.segar.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.segar.backend.config.TestGoogleCloudConfig;

import java.time.Instant;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para la autenticación con Keycloak
 * Verifica que la seguridad JWT funcione correctamente con tokens de Keycloak
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestGoogleCloudConfig.class)
@Disabled("Integration tests disabled - require real Keycloak and JavaMailSender dependencies")
class KeycloakIntegrationTest {

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
    void deberiaPermitirAccesoAEndpointsPublicos() throws Exception {
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRechazarAccesoSinToken() throws Exception {
        mockMvc.perform(get("/api/tramites"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/documentos"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaAceptarTokenValidoDeAdmin() throws Exception {
        Jwt jwt = createJwtWithRole("admin");

        mockMvc.perform(get("/api/admin/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tramites")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaAceptarTokenValidoDeEmpleado() throws Exception {
        Jwt jwt = createJwtWithRole("Empleado");

        mockMvc.perform(get("/api/tramites")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documentos")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isOk());
    }

    @Test
    void empleadoNoDeberiaAccederAEndpointsAdmin() throws Exception {
        Jwt jwt = createJwtWithRole("Empleado");

        mockMvc.perform(get("/api/admin/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRechazarTokenSinRoles() throws Exception {
        Jwt jwt = createJwtWithoutRoles();

        mockMvc.perform(get("/api/tramites")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRechazarTokenConRolInvalido() throws Exception {
        Jwt jwt = createJwtWithRole("INVALID_ROLE");

        mockMvc.perform(get("/api/tramites")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deberiaPermitirCORSParaOrigenesPermitidos() throws Exception {
        mockMvc.perform(options("/api/tramites")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    /**
     * Crea un JWT mock con un rol específico simulando la estructura de Keycloak
     */
    private Jwt createJwtWithRole(String role) {
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", java.util.List.of(role)
                )
        );

        return Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("sub", "test-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("aud", "segar-backend")
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("preferred_username", "test.user")
                .claim("email", "test@segar.gov.co")
                .claim("resource_access", resourceAccess)
                .build();
    }

    /**
     * Crea un JWT mock sin roles
     */
    private Jwt createJwtWithoutRoles() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("sub", "test-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("aud", "segar-backend")
                .claim("exp", Instant.now().plusSeconds(3600))
                .claim("iat", Instant.now())
                .claim("preferred_username", "test.user")
                .claim("email", "test@segar.gov.co")
                .build();
    }
}