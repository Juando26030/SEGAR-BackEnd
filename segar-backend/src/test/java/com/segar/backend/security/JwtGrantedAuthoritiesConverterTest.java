package com.segar.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el convertidor de JWT de Keycloak
 * Verifica que los roles se extraigan correctamente del token JWT
 */
class JwtGrantedAuthoritiesConverterTest {

    private Converter<Jwt, Collection<GrantedAuthority>> converter;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        converter = securityConfig.jwtGrantedAuthoritiesConverter();
    }

    @Test
    void deberiaExtraerRolesDeKeycloakToken() {
        // Given - JWT con rol admin en la estructura de Keycloak
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of("admin", "Empleado")
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).hasSize(2);
        assertThat(authorities).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_EMPLEADO");
    }

    @Test
    void deberiaExtraerSoloRolEmpleado() {
        // Given - JWT con solo rol empleado
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of("Empleado")
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting("authority")
                .containsExactly("ROLE_EMPLEADO");
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayResourceAccess() {
        // Given - JWT sin resource_access
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .claim("iss", "http://localhost:8080/realms/segar")
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).isEmpty();
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHaySegarBackendClient() {
        // Given - JWT con resource_access pero sin segar-backend
        Map<String, Object> resourceAccess = Map.of(
                "otro-cliente", Map.of(
                        "roles", List.of("admin")
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).isEmpty();
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayRoles() {
        // Given - JWT con segar-backend pero sin roles
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "permissions", List.of("read", "write")
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).isEmpty();
    }

    @Test
    void deberiaConvertirRolesAMayusculas() {
        // Given - JWT con roles en minúsculas y mixto
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of("admin", "empleado", "SuperVisor")
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).hasSize(3);
        assertThat(authorities).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_EMPLEADO", "ROLE_SUPERVISOR");
    }

    @Test
    void deberiaManejarListaDeRolesVacia() {
        // Given - JWT con lista de roles vacía
        Map<String, Object> resourceAccess = Map.of(
                "segar-backend", Map.of(
                        "roles", List.of()
                )
        );

        Jwt jwt = createJwt(resourceAccess);

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Then
        assertThat(authorities).isEmpty();
    }

    private Jwt createJwt(Map<String, Object> resourceAccess) {
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
}