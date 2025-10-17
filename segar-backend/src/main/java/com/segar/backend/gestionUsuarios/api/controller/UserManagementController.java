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
                request.getLastName(),
                request.getIdType(),
                request.getIdNumber(),
                request.getBirthDate(),
                request.getGender(),
                request.getPhone(),
                request.getAltPhone(),
                request.getAddress(),
                request.getCity(),
                request.getPostalCode(),
                request.getEmployeeId(),
                request.getRole()
        );

        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserRepresentation> kcUsers = keycloakUserService.getAllUsers();

        List<UserResponse> responses = kcUsers.stream()
                .map(this::mapKeycloakUserToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/local")
    public ResponseEntity<List<Usuario>> getAllUsersLocal() {
        List<Usuario> usuarios = usuarioService.getAllUsuariosLocales();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        Usuario usuario = usuarioService.findByUsername(username);
        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(@PathVariable String keycloakId) {
        Usuario usuario = usuarioService.findByKeycloakId(keycloakId);
        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    private UserResponse mapUsuarioToResponse(Usuario usuario) {
        UserRepresentation kcUser = keycloakUserService.getUserById(usuario.getKeycloakId());

        return UserResponse.builder()
                .id(usuario.getId())
                .keycloakId(usuario.getKeycloakId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .firstName(usuario.getFirstName())
                .lastName(usuario.getLastName())
                .fullName(usuario.getFullName())
                .idType(usuario.getIdType())
                .idNumber(usuario.getIdNumber())
                .birthDate(usuario.getBirthDate())
                .gender(usuario.getGender())
                .phone(usuario.getPhone())
                .altPhone(usuario.getAltPhone())
                .address(usuario.getAddress())
                .city(usuario.getCity())
                .postalCode(usuario.getPostalCode())
                .employeeId(usuario.getEmployeeId())
                .role(usuario.getRole())
                .enabled(kcUser.isEnabled())
                .fechaRegistro(usuario.getFechaRegistro())
                .activo(usuario.getActivo())
                .build();
    }

    private UserResponse mapKeycloakUserToResponse(UserRepresentation kcUser) {
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
