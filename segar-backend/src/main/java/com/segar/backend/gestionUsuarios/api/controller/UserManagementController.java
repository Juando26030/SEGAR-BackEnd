package com.segar.backend.gestionUsuarios.api.controller;

import com.segar.backend.gestionUsuarios.api.dto.CreateUserRequest;
import com.segar.backend.gestionUsuarios.api.dto.UserResponse;
import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.service.KeycloakUserService;
import com.segar.backend.gestionUsuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UserManagementController {

    private final UsuarioService usuarioService;
    private final KeycloakUserService keycloakUserService;

    public UserManagementController(UsuarioService usuarioService,
                                    KeycloakUserService keycloakUserService) {
        this.usuarioService = usuarioService;
        this.keycloakUserService = keycloakUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        Usuario usuario = usuarioService.createUsuarioCompleto(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
        );

        return ResponseEntity.ok(mapToResponse(usuario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserRepresentation> kcUsers = keycloakUserService.getAllUsers();

        List<UserResponse> responses = kcUsers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    private UserResponse mapToResponse(Usuario usuario) {
        // Obtener datos completos de Keycloak
        UserRepresentation kcUser = keycloakUserService.getUserById(usuario.getKeycloakId());

        return UserResponse.builder()
                .id(usuario.getId())
                .keycloakId(usuario.getKeycloakId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .firstName(kcUser.getFirstName())
                .lastName(kcUser.getLastName())
                .enabled(kcUser.isEnabled())
                .fechaRegistro(usuario.getFechaRegistro())
                .build();
    }

    private UserResponse mapToResponse(UserRepresentation kcUser) {
        return UserResponse.builder()
                .keycloakId(kcUser.getId())
                .username(kcUser.getUsername())
                .email(kcUser.getEmail())
                .firstName(kcUser.getFirstName())
                .lastName(kcUser.getLastName())
                .enabled(kcUser.isEnabled())
                .build();
    }
}
