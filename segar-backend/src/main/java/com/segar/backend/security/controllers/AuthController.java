package com.segar.backend.security.controllers;

import com.segar.backend.security.dto.UserInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @GetMapping("/auth/user-info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        // Extraer roles de Keycloak
        List<String> roles = extractRoles(jwt);
        
        UserInfoDTO userInfo = new UserInfoDTO(
            username,
            email,
            firstName,
            lastName,
            roles,
            true
        );
        
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAllUsers() {
        // Aquí implementarías la lógica para obtener usuarios desde Keycloak
        // Por ahora devuelvo un mensaje indicativo
        return ResponseEntity.ok("Lista de usuarios - Solo accesible para administradores");
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> userData) {
        // Aquí implementarías la lógica para crear un usuario en Keycloak
        return ResponseEntity.ok("Usuario creado - Solo accesible para administradores");
    }

    @PutMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody Map<String, Object> userData) {
        // Aquí implementarías la lógica para actualizar un usuario en Keycloak
        return ResponseEntity.ok("Usuario actualizado - Solo accesible para administradores");
    }

    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        // Aquí implementarías la lógica para eliminar un usuario en Keycloak
        return ResponseEntity.ok("Usuario eliminado - Solo accesible para administradores");
    }

    @GetMapping("/usuarios/perfil")
    public ResponseEntity<UserInfoDTO> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        // Tanto admin como empleado pueden ver su propio perfil
        return getUserInfo(jwt);
    }

    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess == null) {
            return List.of();
        }

        Map<String, Object> segarClient = (Map<String, Object>) resourceAccess.get("segar-backend");
        if (segarClient == null) {
            return List.of();
        }

        List<String> roles = (List<String>) segarClient.get("roles");
        return roles != null ? roles : List.of();
    }
}