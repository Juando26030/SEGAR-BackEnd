package com.segar.backend.gestionUsuarios.api.controller;

import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.service.KeycloakUserService;
import com.segar.backend.gestionUsuarios.service.UsuarioService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios/admin")
public class UsuarioAdminController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminController.class);

    private final UsuarioService usuarioService;
    private final KeycloakUserService keycloakUserService;

    public UsuarioAdminController(UsuarioService usuarioService,
                                   KeycloakUserService keycloakUserService) {
        this.usuarioService = usuarioService;
        this.keycloakUserService = keycloakUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/check-sync")
    public ResponseEntity<Map<String, Object>> checkSynchronization() {
        logger.info("🔍 Verificando sincronización entre BD local y Keycloak...");

        List<Usuario> usuariosLocales = usuarioService.getAllUsuariosLocales();
        List<Map<String, Object>> usuariosHuerfanos = new ArrayList<>();
        List<Map<String, Object>> usuariosSincronizados = new ArrayList<>();

        for (Usuario usuario : usuariosLocales) {
            // Consultar DIRECTAMENTE a Keycloak por username (fuente de verdad)
            logger.info("🔍 Consultando Keycloak para usuario: {}", usuario.getUsername());
            Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

            if (kcUserOpt.isEmpty()) {
                logger.warn("⚠️ Usuario huérfano encontrado: ID={}, username={}, keycloakId={}",
                        usuario.getId(), usuario.getUsername(), usuario.getKeycloakId());

                usuariosHuerfanos.add(Map.of(
                        "id", usuario.getId(),
                        "username", usuario.getUsername(),
                        "email", usuario.getEmail(),
                        "keycloakId", usuario.getKeycloakId(),
                        "estado", "NO_EXISTE_EN_KEYCLOAK"
                ));
            } else {
                String keycloakIdReal = kcUserOpt.get().getId();

                // Verificar si el keycloakId está desincronizado
                if (!keycloakIdReal.equals(usuario.getKeycloakId())) {
                    logger.warn("⚠️ keycloakId desincronizado para usuario '{}': BD={}, Keycloak={}",
                            usuario.getUsername(), usuario.getKeycloakId(), keycloakIdReal);

                    usuariosSincronizados.add(Map.of(
                            "id", usuario.getId(),
                            "username", usuario.getUsername(),
                            "estado", "DESINCRONIZADO",
                            "keycloakIdLocal", usuario.getKeycloakId(),
                            "keycloakIdReal", keycloakIdReal
                    ));
                } else {
                    usuariosSincronizados.add(Map.of(
                            "id", usuario.getId(),
                            "username", usuario.getUsername(),
                            "estado", "SINCRONIZADO",
                            "keycloakId", keycloakIdReal
                    ));
                }
            }
        }

        logger.info("✅ Verificación completa. Huérfanos: {}, Sincronizados: {}",
                usuariosHuerfanos.size(), usuariosSincronizados.size());

        return ResponseEntity.ok(Map.of(
                "totalLocales", usuariosLocales.size(),
                "sincronizados", usuariosSincronizados,
                "huerfanos", usuariosHuerfanos,
                "mensaje", usuariosHuerfanos.isEmpty()
                        ? "Todos los usuarios están sincronizados correctamente"
                        : "Se encontraron usuarios que no existen en Keycloak. Elimínelos y vuélvalos a crear."
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cleanup/{id}")
    public ResponseEntity<Map<String, String>> cleanupOrphanUser(@PathVariable Long id) {
        logger.info("🗑️ Eliminando usuario huérfano con ID: {}", id);

        try {
            Usuario usuario = usuarioService.findById(id);

            // Verificar que efectivamente no existe en Keycloak consultando directamente
            logger.info("🔍 Verificando en Keycloak usuario: {}", usuario.getUsername());
            Optional<UserRepresentation> kcUserOpt = keycloakUserService.getUserByUsername(usuario.getUsername());

            if (kcUserOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "El usuario '" + usuario.getUsername() + "' SÍ existe en Keycloak. Use el endpoint DELETE normal."
                ));
            }

            // Solo eliminar de BD local (no existe en Keycloak)
            usuarioService.deleteUsuarioLocal(id);

            logger.info("✅ Usuario huérfano eliminado de BD local: {}", usuario.getUsername());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario huérfano eliminado exitosamente de la base de datos local",
                    "username", usuario.getUsername()
            ));

        } catch (Exception e) {
            logger.error("❌ Error al eliminar usuario huérfano: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error al eliminar usuario: " + e.getMessage()
            ));
        }
    }
}
