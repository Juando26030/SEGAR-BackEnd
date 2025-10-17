package com.segar.backend.gestionUsuarios.service;

import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.infrastructure.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final KeycloakUserService keycloakUserService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          KeycloakUserService keycloakUserService) {
        this.usuarioRepository = usuarioRepository;
        this.keycloakUserService = keycloakUserService;
    }

    @Transactional
    public Usuario createUsuarioCompleto(String username, String email, String password,
                                         String firstName, String lastName,
                                         String idType, String idNumber, LocalDate birthDate, String gender,
                                         String phone, String altPhone, String address, String city, String postalCode,
                                         String employeeId, String role) {
        logger.info("🔵 Iniciando creación de usuario: {}", username);

        // 1. Crear usuario en Keycloak (autenticación)
        String keycloakId = keycloakUserService.createUser(
                username, email, password, firstName, lastName
        );
        logger.info("🟢 Usuario creado en Keycloak con ID: {}", keycloakId);

        // 2. Crear registro local con toda la información de negocio
        Usuario usuario = new Usuario();

        // Vinculación con Keycloak
        usuario.setKeycloakId(keycloakId);
        usuario.setUsername(username);
        usuario.setEmail(email);

        // Información Personal
        usuario.setFirstName(firstName);
        usuario.setLastName(lastName);
        usuario.setIdType(idType);
        usuario.setIdNumber(idNumber);
        usuario.setBirthDate(birthDate);
        usuario.setGender(gender);

        // Información de Contacto
        usuario.setPhone(phone);
        usuario.setAltPhone(altPhone);
        usuario.setAddress(address);
        usuario.setCity(city);
        usuario.setPostalCode(postalCode);

        // Información Laboral
        usuario.setEmployeeId(employeeId);
        usuario.setRole(role);

        // Auditoría
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        logger.info("✅ Usuario guardado en H2 con ID local: {} y keycloakId: {}",
                savedUsuario.getId(), savedUsuario.getKeycloakId());

        return savedUsuario;
    }

    public List<Usuario> getAllUsuariosLocales() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.info("📋 Consultando usuarios locales en H2. Total encontrados: {}", usuarios.size());
        return usuarios;
    }

    public Usuario findById(Long id) {
        logger.info("🔍 Buscando usuario por ID local: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public Usuario findByUsername(String username) {
        logger.info("🔍 Buscando usuario por username: {}", username);
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
    }

    public Usuario findByKeycloakId(String keycloakId) {
        logger.info("🔍 Buscando usuario por keycloakId: {}", keycloakId);
        return usuarioRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con keycloakId: " + keycloakId));
    }
}
