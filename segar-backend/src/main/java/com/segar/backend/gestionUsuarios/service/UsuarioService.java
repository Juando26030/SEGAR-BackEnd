package com.segar.backend.gestionUsuarios.service;

import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final KeycloakUserService keycloakUserService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          KeycloakUserService keycloakUserService) {
        this.usuarioRepository = usuarioRepository;
        this.keycloakUserService = keycloakUserService;
    }

    @Transactional
    public Usuario createUsuarioCompleto(String username, String email,
                                         String password, String firstName,
                                         String lastName) {
        String keycloakId = keycloakUserService.createUser(
                username, email, password, firstName, lastName
        );

        Usuario usuario = new Usuario();
        usuario.setKeycloakId(keycloakId);
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public Usuario findByKeycloakId(String keycloakId) {
        return usuarioRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
