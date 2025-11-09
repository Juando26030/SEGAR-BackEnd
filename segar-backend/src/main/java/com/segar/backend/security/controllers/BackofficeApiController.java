package com.segar.backend.security.controllers;

import com.segar.backend.security.dto.AdminLoginRequest;
import com.segar.backend.security.dto.AdminLoginResponse;
import com.segar.backend.security.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación de administradores
 */
@RestController
@RequestMapping("/api/backoffice")
public class BackofficeApiController {

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * Login para administradores con Keycloak
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        try {
            AdminLoginResponse response = adminAuthService.authenticateAdmin(
                request.getUsername(),
                request.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(new AdminLoginResponse(false, null, null, "Credenciales inválidas: " + e.getMessage()));
        }
    }

    /**
     * Verificar si el usuario autenticado es SUPER_ADMIN
     */
    @GetMapping("/admin/verify")
    public ResponseEntity<AdminLoginResponse> verifyAdmin(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401)
                .body(new AdminLoginResponse(false, null, null, "No autenticado"));
        }

        String username = jwt.getClaimAsString("preferred_username");
        boolean isSuperAdmin = adminAuthService.isSuperAdmin(jwt);

        if (isSuperAdmin) {
            return ResponseEntity.ok(new AdminLoginResponse(
                true,
                username,
                "SUPER_ADMIN",
                "Acceso autorizado"
            ));
        } else {
            return ResponseEntity.status(403)
                .body(new AdminLoginResponse(false, username, null, "No tiene permisos de SUPER_ADMIN"));
        }
    }

    /**
     * Endpoint de prueba solo para SUPER_ADMIN
     */
    @GetMapping("/admin/dashboard/stats")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        // TODO: Implementar estadísticas del sistema
        return ResponseEntity.ok()
            .body(java.util.Map.of(
                "message", "Dashboard de Super Admin",
                "totalUsuarios", 0,
                "totalTramites", 0,
                "status", "En desarrollo"
            ));
    }
}

