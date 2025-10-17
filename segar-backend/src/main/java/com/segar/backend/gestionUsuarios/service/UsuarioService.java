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

    @Transactional
    public Usuario updateUsuario(Long id, String email, String firstName, String lastName,
                                String idType, String idNumber, LocalDate birthDate, String gender,
                                String phone, String altPhone, String address, String city, String postalCode,
                                String employeeId, String role, Boolean enabled) {
        logger.info("🔵 Actualizando usuario con ID: {}", id);

        // 1. Buscar usuario local
        Usuario usuario = findById(id);
        String keycloakId = usuario.getKeycloakId();

        // 2. Actualizar en Keycloak (datos de autenticación)
        keycloakUserService.updateUser(keycloakId, email, firstName, lastName, enabled);
        logger.info("🟢 Usuario actualizado en Keycloak con ID: {}", keycloakId);

        // 3. Actualizar datos locales
        if (email != null) usuario.setEmail(email);
        if (firstName != null) usuario.setFirstName(firstName);
        if (lastName != null) usuario.setLastName(lastName);
        if (idType != null) usuario.setIdType(idType);
        if (idNumber != null) usuario.setIdNumber(idNumber);
        if (birthDate != null) usuario.setBirthDate(birthDate);
        if (gender != null) usuario.setGender(gender);
        if (phone != null) usuario.setPhone(phone);
        if (altPhone != null) usuario.setAltPhone(altPhone);
        if (address != null) usuario.setAddress(address);
        if (city != null) usuario.setCity(city);
        if (postalCode != null) usuario.setPostalCode(postalCode);
        if (employeeId != null) usuario.setEmployeeId(employeeId);
        if (role != null) usuario.setRole(role);

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        logger.info("✅ Usuario actualizado en base de datos local con ID: {}", updatedUsuario.getId());

        return updatedUsuario;
    }

    @Transactional
    public void updatePassword(Long id, String newPassword, boolean temporary) {
        logger.info("🔵 Actualizando contraseña del usuario con ID: {}", id);

        Usuario usuario = findById(id);
        keycloakUserService.updatePassword(usuario.getKeycloakId(), newPassword, temporary);

        logger.info("✅ Contraseña actualizada en Keycloak para usuario: {}", usuario.getUsername());
    }

    @Transactional
    public void deleteUsuario(Long id) {
        logger.info("🔵 Eliminando usuario con ID: {}", id);

        // 1. Buscar usuario local
        Usuario usuario = findById(id);
        String keycloakId = usuario.getKeycloakId();

        // 2. Eliminar de Keycloak
        keycloakUserService.deleteUser(keycloakId);
        logger.info("🟢 Usuario eliminado de Keycloak con ID: {}", keycloakId);

        // 3. Eliminar registro local
        usuarioRepository.delete(usuario);
        logger.info("✅ Usuario eliminado de la base de datos local con ID: {}", id);
    }

    @Transactional
    public Usuario toggleUsuarioActivo(Long id) {
        logger.info("🔵 Cambiando estado activo del usuario con ID: {}", id);

        Usuario usuario = findById(id);
        boolean nuevoEstado = !usuario.getActivo();

        // Actualizar en Keycloak
        keycloakUserService.enableUser(usuario.getKeycloakId(), nuevoEstado);

        // Actualizar localmente
        usuario.setActivo(nuevoEstado);
        Usuario updatedUsuario = usuarioRepository.save(usuario);

        logger.info("✅ Estado activo cambiado a {} para usuario: {}", nuevoEstado, usuario.getUsername());

        return updatedUsuario;
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
