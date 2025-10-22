package com.segar.backend.gestionUsuarios.service;

import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.infrastructure.repository.UsuarioRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        logger.info("🔵 Iniciando creación de usuario: {} con rol: {}", username, role);

        // 1. Crear usuario en Keycloak (autenticación) con asignación de roles
        String keycloakId = keycloakUserService.createUser(
                username, email, password, firstName, lastName, role
        );
        logger.info("🟢 Usuario creado en Keycloak con ID: {} y roles asignados", keycloakId);

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

        // 2. Buscar usuario DIRECTAMENTE en Keycloak por username
        logger.info("🔍 Consultando directamente a Keycloak para usuario: {}", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isEmpty()) {
            logger.error("❌ Usuario '{}' no encontrado en Keycloak. Debe recrearse.", usuario.getUsername());
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' no existe en Keycloak. Por favor, elimínelo y vuélvalo a crear.");
        }

        String keycloakIdReal = kcUserOpt.get().getId();
        logger.info("✅ Usuario encontrado en Keycloak con ID: {}", keycloakIdReal);

        // 3. Sincronizar keycloakId si cambió
        if (!keycloakIdReal.equals(usuario.getKeycloakId())) {
            logger.warn("⚠️ keycloakId desincronizado. Actualizando: {} -> {}",
                    usuario.getKeycloakId(), keycloakIdReal);
            usuario.setKeycloakId(keycloakIdReal);
        }

        // 4. Actualizar en Keycloak (datos de autenticación)
        keycloakUserService.updateUser(keycloakIdReal, email, firstName, lastName, enabled);
        logger.info("🟢 Usuario actualizado en Keycloak");

        // 5. Actualizar datos locales
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
        if (enabled != null) usuario.setActivo(enabled);

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        logger.info("✅ Usuario actualizado localmente");

        return updatedUsuario;
    }

    @Transactional
    public void updatePassword(Long id, String newPassword, boolean temporary) {
        logger.info("🔵 Actualizando contraseña del usuario con ID: {}", id);

        Usuario usuario = findById(id);

        // Buscar usuario DIRECTAMENTE en Keycloak por username
        logger.info("🔍 Consultando directamente a Keycloak para usuario: {}", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isEmpty()) {
            logger.error("❌ Usuario '{}' no encontrado en Keycloak. Debe recrearse.", usuario.getUsername());
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' no existe en Keycloak. Por favor, elimínelo de la base de datos y vuélvalo a crear.");
        }

        String keycloakIdReal = kcUserOpt.get().getId();
        logger.info("✅ Usuario encontrado en Keycloak con ID: {}", keycloakIdReal);

        // Sincronizar keycloakId si cambió
        if (!keycloakIdReal.equals(usuario.getKeycloakId())) {
            logger.warn("⚠️ keycloakId desincronizado. Actualizando: {} -> {}",
                    usuario.getKeycloakId(), keycloakIdReal);
            usuario.setKeycloakId(keycloakIdReal);
            usuarioRepository.save(usuario);
        }

        keycloakUserService.updatePassword(keycloakIdReal, newPassword, temporary);

        logger.info("✅ Contraseña actualizada en Keycloak para usuario: {}", usuario.getUsername());
    }

    @Transactional
    public Usuario toggleUsuarioActivo(Long id) {
        logger.info("🔵 Cambiando estado activo del usuario con ID: {}", id);

        Usuario usuario = findById(id);

        // Buscar usuario DIRECTAMENTE en Keycloak por username
        logger.info("🔍 Consultando directamente a Keycloak para usuario: {}", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isEmpty()) {
            logger.error("❌ Usuario '{}' no encontrado en Keycloak. Debe recrearse.", usuario.getUsername());
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' no existe en Keycloak. Por favor, elimínelo y vuélvalo a crear.");
        }

        String keycloakIdReal = kcUserOpt.get().getId();
        logger.info("✅ Usuario encontrado en Keycloak con ID: {}", keycloakIdReal);

        // Sincronizar keycloakId si cambió
        if (!keycloakIdReal.equals(usuario.getKeycloakId())) {
            logger.warn("⚠️ keycloakId desincronizado. Actualizando: {} -> {}",
                    usuario.getKeycloakId(), keycloakIdReal);
            usuario.setKeycloakId(keycloakIdReal);
        }

        boolean nuevoEstado = !usuario.getActivo();

        keycloakUserService.enableUser(keycloakIdReal, nuevoEstado);
        logger.info("🟢 Estado enabled actualizado a {} en Keycloak", nuevoEstado);

        // Actualizar localmente
        usuario.setActivo(nuevoEstado);
        Usuario updatedUsuario = usuarioRepository.save(usuario);

        logger.info("✅ Estado activo cambiado a {} para usuario: {}", nuevoEstado, usuario.getUsername());

        return updatedUsuario;
    }

    @Transactional
    public void deleteUsuario(Long id) {
        logger.info("🔵 Eliminando usuario con ID: {}", id);

        // 1. Buscar usuario local
        Usuario usuario = findById(id);

        // 2. Buscar usuario DIRECTAMENTE en Keycloak por username
        logger.info("🔍 Consultando directamente a Keycloak para usuario: {}", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isPresent()) {
            String keycloakIdReal = kcUserOpt.get().getId();
            logger.info("✅ Usuario encontrado en Keycloak con ID: {}", keycloakIdReal);

            // 3. Eliminar de Keycloak
            keycloakUserService.deleteUser(keycloakIdReal);
            logger.info("🟢 Usuario eliminado de Keycloak");
        } else {
            logger.warn("⚠️ Usuario '{}' no encontrado en Keycloak, solo eliminando de BD local", usuario.getUsername());
        }

        // 4. Eliminar registro local
        usuarioRepository.delete(usuario);
        logger.info("✅ Usuario eliminado de la base de datos local con ID: {}", id);
    }

    @Transactional
    public void deleteUsuarioLocal(Long id) {
        logger.info("🔵 Eliminando usuario SOLO de BD local (huérfano) con ID: {}", id);

        Usuario usuario = findById(id);

        // Verificar que NO existe en Keycloak
        logger.info("🔍 Verificando que usuario '{}' NO existe en Keycloak", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isPresent()) {
            logger.error("❌ El usuario '{}' SÍ existe en Keycloak. Use el endpoint DELETE normal.", usuario.getUsername());
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' SÍ existe en Keycloak. Use el endpoint DELETE normal en lugar del cleanup.");
        }

        // Solo eliminar de BD local
        usuarioRepository.delete(usuario);
        logger.info("✅ Usuario huérfano eliminado de BD local: {}", usuario.getUsername());
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

    /**
     * Habilita el login de un usuario limpiando las acciones requeridas en Keycloak.
     * Útil cuando un usuario tiene el error "Account is not fully set up"
     */
    @Transactional
    public void habilitarLoginUsuario(Long id) {
        logger.info("🔧 Habilitando login para usuario con ID: {}", id);

        Usuario usuario = findById(id);

        // Buscar usuario DIRECTAMENTE en Keycloak por username
        logger.info("🔍 Consultando directamente a Keycloak para usuario: {}", usuario.getUsername());
        Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

        if (kcUserOpt.isEmpty()) {
            logger.error("❌ Usuario '{}' no encontrado en Keycloak.", usuario.getUsername());
            throw new RuntimeException("El usuario '" + usuario.getUsername() +
                    "' no existe en Keycloak. Por favor, elimínelo y vuélvalo a crear.");
        }

        String keycloakIdReal = kcUserOpt.get().getId();
        logger.info("✅ Usuario encontrado en Keycloak con ID: {}", keycloakIdReal);

        // Sincronizar keycloakId si cambió
        if (!keycloakIdReal.equals(usuario.getKeycloakId())) {
            logger.warn("⚠️ keycloakId desincronizado. Actualizando: {} -> {}",
                    usuario.getKeycloakId(), keycloakIdReal);
            usuario.setKeycloakId(keycloakIdReal);
            usuarioRepository.save(usuario);
        }

        // Limpiar acciones requeridas en Keycloak
        keycloakUserService.clearRequiredActions(keycloakIdReal);

        logger.info("✅ Usuario habilitado para login: {}", usuario.getUsername());
    }
}
