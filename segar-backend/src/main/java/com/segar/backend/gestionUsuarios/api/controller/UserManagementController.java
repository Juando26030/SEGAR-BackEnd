package com.segar.backend.gestionUsuarios.api.controller;

import com.segar.backend.gestionUsuarios.api.dto.CreateUserRequest;
import com.segar.backend.gestionUsuarios.api.dto.UpdateUserRequest;
import com.segar.backend.gestionUsuarios.api.dto.UpdatePasswordRequest;
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
import java.util.Map;
import java.util.Optional;
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
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        Usuario usuario = usuarioService.updateUsuario(
                id,
                request.getEmail(),
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
                request.getRole(),
                request.getEnabled()
        );

        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request) {

        usuarioService.updatePassword(id, request.getNewPassword(), request.getTemporary());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<UserResponse> toggleUserActive(@PathVariable Long id) {
        Usuario usuario = usuarioService.toggleUsuarioActivo(id);
        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/habilitar-login")
    public ResponseEntity<Map<String, String>> habilitarLogin(@PathVariable Long id) {
        usuarioService.habilitarLoginUsuario(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario habilitado exitosamente para hacer login",
                "descripcion", "Se han limpiado todas las acciones requeridas en Keycloak"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
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
        return ResponseEntity.ok(mapUsuarioToResponseSafe(usuario));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(@PathVariable String keycloakId) {
        Usuario usuario = usuarioService.findByKeycloakId(keycloakId);
        return ResponseEntity.ok(mapUsuarioToResponse(usuario));
    }

    private UserResponse mapUsuarioToResponse(Usuario usuario) {
        // Consultar DIRECTAMENTE a Keycloak por username (fuente de verdad)
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isEmpty()) {
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' no existe en Keycloak. Por favor, elimínelo y vuélvalo a crear.");
        }

        UserRepresentation kcUser = kcUserOpt.get();
        String keycloakIdReal = kcUser.getId();

        return UserResponse.builder()
                .id(usuario.getId())
                .keycloakId(keycloakIdReal)
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

    private UserResponse mapUsuarioToResponseSafe(Usuario usuario) {
        // Consultar DIRECTAMENTE a Keycloak por username (fuente de verdad)
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(usuario.getId())
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
                .fechaRegistro(usuario.getFechaRegistro())
                .activo(usuario.getActivo());

        // Si Keycloak responde, usar sus datos como fuente de verdad
        if (kcUserOpt.isPresent()) {
            UserRepresentation kcUser = kcUserOpt.get();
            builder.keycloakId(kcUser.getId());
            builder.enabled(kcUser.isEnabled());
        } else {
            // Si no existe en Keycloak, usar datos locales
            builder.keycloakId(usuario.getKeycloakId());
            builder.enabled(usuario.getActivo());
        }

        return builder.build();
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
