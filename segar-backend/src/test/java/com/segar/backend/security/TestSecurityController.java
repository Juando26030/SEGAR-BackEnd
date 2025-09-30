package com.segar.backend.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller de prueba para testing de seguridad con Keycloak
 * Solo se usa en tests, no en producción
 */
@RestController
@RequestMapping("/api/test")
@Component
@Profile("test")
public class TestSecurityController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Endpoint público accesible");
    }

    @GetMapping("/authenticated")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt != null ? jwt.getClaimAsString("preferred_username") : "N/A";
        String email = jwt != null ? jwt.getClaimAsString("email") : "N/A";
        
        // Evitar nulls en Map.of()
        username = username != null ? username : "N/A";
        email = email != null ? email : "N/A";
        
        return ResponseEntity.ok(Map.of(
                "message", "Usuario autenticado",
                "username", username,
                "email", email
        ));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Endpoint solo para administradores");
    }

    @GetMapping("/empleado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<String> empleadoEndpoint() {
        return ResponseEntity.ok("Endpoint para empleados");
    }

    @GetMapping("/admin-or-empleado")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<String> adminOrEmpleadoEndpoint() {
        return ResponseEntity.ok("Endpoint para admin o empleado");
    }
}