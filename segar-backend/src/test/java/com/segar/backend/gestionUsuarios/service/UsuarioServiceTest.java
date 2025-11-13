package com.segar.backend.gestionUsuarios.service;

import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.gestionUsuarios.infrastructure.repository.UsuarioRepository;
import com.segar.backend.shared.domain.Empresa;
import com.segar.backend.shared.infrastructure.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsuarioService
 * Verifica la sincronización bidireccional con Keycloak y gestión de usuarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Gestión de Usuarios - Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KeycloakUserService keycloakUserService;

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private Usuario usuarioMock;
    private UserRepresentation keycloakUserMock;
    private Empresa empresaMock;

    @BeforeEach
    void setUp() {
        // Inyectar el empresaRepository usando ReflectionTestUtils
        ReflectionTestUtils.setField(usuarioService, "empresaRepository", empresaRepository);

        // Configurar usuario mock
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setKeycloakId("keycloak-123");
        usuarioMock.setUsername("testuser");
        usuarioMock.setEmail("test@example.com");
        usuarioMock.setFirstName("Test");
        usuarioMock.setLastName("User");
        usuarioMock.setIdType("CC");
        usuarioMock.setIdNumber("1234567890");
        usuarioMock.setBirthDate(LocalDate.of(1990, 1, 1));
        usuarioMock.setGender("M");
        usuarioMock.setPhone("3001234567");
        usuarioMock.setRole("EMPLEADO");
        usuarioMock.setActivo(true);
        usuarioMock.setFechaRegistro(LocalDateTime.now());
        usuarioMock.setEmpresaId(100L);

        // Configurar Keycloak user mock
        keycloakUserMock = new UserRepresentation();
        keycloakUserMock.setId("keycloak-123");
        keycloakUserMock.setUsername("testuser");
        keycloakUserMock.setEmail("test@example.com");
        keycloakUserMock.setFirstName("Test");
        keycloakUserMock.setLastName("User");
        keycloakUserMock.setEnabled(true);

        // Configurar empresa mock
        empresaMock = new Empresa();
        empresaMock.setId(100L);
        empresaMock.setNit("900123456");
        empresaMock.setRazonSocial("Empresa Test S.A.S.");
    }

    // ==================== TESTS DE CREACIÓN DE USUARIOS ====================

    @Test
    @DisplayName("Crear usuario completo debe sincronizar con Keycloak")
    void testCrearUsuarioCompleto_DebeSincronizarConKeycloak() {
        // Given
        when(keycloakUserService.createUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn("keycloak-new-id");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.createUsuarioCompleto(
                "newuser", "new@example.com", "password123",
                "New", "User", "CC", "9876543210",
                LocalDate.of(1995, 5, 15), "F",
                "3009876543", null, "Calle 123", "Bogotá", "110111",
                "EMP001", "ADMIN"
        );

        // Then
        assertThat(result).isNotNull();
        verify(keycloakUserService).createUser(
                eq("newuser"), eq("new@example.com"), eq("password123"),
                eq("New"), eq("User"), eq("ADMIN")
        );
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getKeycloakId()).isEqualTo("keycloak-new-id");
        assertThat(savedUsuario.getUsername()).isEqualTo("newuser");
        assertThat(savedUsuario.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUsuario.getRole()).isEqualTo("ADMIN");
        assertThat(savedUsuario.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Crear usuario debe establecer fecha de registro")
    void testCrearUsuario_DebeEstablecerFechaRegistro() {
        // Given
        when(keycloakUserService.createUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn("keycloak-new-id");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Usuario result = usuarioService.createUsuarioCompleto(
                "newuser", "new@example.com", "password123",
                "New", "User", "CC", "9876543210",
                LocalDate.of(1995, 5, 15), "F",
                "3009876543", null, "Calle 123", "Bogotá", "110111",
                "EMP001", "EMPLEADO"
        );
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);

        // Then
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getFechaRegistro())
                .isAfter(antes)
                .isBefore(despues);
    }

    @Test
    @DisplayName("Error en Keycloak debe propagar excepción")
    void testErrorEnKeycloak_DebePropagar() {
        // Given
        when(keycloakUserService.createUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenThrow(new RuntimeException("Keycloak Error"));

        // When & Then
        assertThatThrownBy(() -> usuarioService.createUsuarioCompleto(
                "newuser", "new@example.com", "password123",
                "New", "User", "CC", "9876543210",
                LocalDate.of(1995, 5, 15), "F",
                "3009876543", null, "Calle 123", "Bogotá", "110111",
                "EMP001", "EMPLEADO"
        )).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Keycloak Error");
    }

    // ==================== TESTS DE ACTUALIZACIÓN DE USUARIOS ====================

    @Test
    @DisplayName("Actualizar usuario debe sincronizar con Keycloak")
    void testActualizarUsuario_DebeSincronizar() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).updateUser(anyString(), anyString(), anyString(), anyString(), anyBoolean());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.updateUsuario(
                1L, "updated@example.com", "Updated", "User",
                "CE", "9876543210", LocalDate.of(1990, 2, 2), "F",
                "3119876543", "3209876543", "Carrera 456", "Medellín", "050001",
                "EMP002", "ADMIN", true
        );

        // Then
        assertThat(result).isNotNull();
        verify(keycloakUserService).updateUser(
                eq("keycloak-123"), eq("updated@example.com"), eq("Updated"), eq("User"), eq(true)
        );
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar con keycloakId desincronizado debe corregir")
    void testActualizarConIdDesincronizado_DebeCorregir() {
        // Given
        usuarioMock.setKeycloakId("old-keycloak-id");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setId("new-keycloak-id");
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(kcUser));

        doNothing().when(keycloakUserService).updateUser(anyString(), anyString(), anyString(), anyString(), anyBoolean());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.updateUsuario(
                1L, "test@example.com", "Test", "User",
                null, null, null, null, null, null, null, null, null,
                null, "EMPLEADO", true
        );

        // Then
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getKeycloakId()).isEqualTo("new-keycloak-id");
    }

    @Test
    @DisplayName("Actualizar usuario inexistente en Keycloak debe lanzar excepción")
    void testActualizarUsuarioNoExisteEnKeycloak() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.updateUsuario(
                1L, "test@example.com", "Test", "User",
                null, null, null, null, null, null, null, null, null,
                null, "EMPLEADO", true
        )).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe en Keycloak");
    }

    // ==================== TESTS DE CAMBIO DE CONTRASEÑA ====================

    @Test
    @DisplayName("Actualizar contraseña debe funcionar correctamente")
    void testActualizarPassword() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).updatePassword(anyString(), anyString(), anyBoolean());

        // When
        usuarioService.updatePassword(1L, "newPassword123", false);

        // Then
        verify(keycloakUserService).updatePassword("keycloak-123", "newPassword123", false);
    }

    @Test
    @DisplayName("Actualizar contraseña temporal debe establecer flag")
    void testActualizarPasswordTemporal() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).updatePassword(anyString(), anyString(), anyBoolean());

        // When
        usuarioService.updatePassword(1L, "tempPassword", true);

        // Then
        verify(keycloakUserService).updatePassword("keycloak-123", "tempPassword", true);
    }

    // ==================== TESTS DE ACTIVACIÓN/DESACTIVACIÓN ====================

    @Test
    @DisplayName("Toggle activo debe cambiar estado en Keycloak y BD")
    void testToggleActivo_DebeCambiarEstado() {
        // Given
        usuarioMock.setActivo(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).enableUser(anyString(), anyBoolean());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.toggleUsuarioActivo(1L);

        // Then
        verify(keycloakUserService).enableUser("keycloak-123", false);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Toggle activo debe activar usuario desactivado")
    void testToggleActivo_DebeActivar() {
        // Given
        usuarioMock.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).enableUser(anyString(), anyBoolean());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.toggleUsuarioActivo(1L);

        // Then
        verify(keycloakUserService).enableUser("keycloak-123", true);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getActivo()).isTrue();
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    @Test
    @DisplayName("Eliminar usuario debe borrar de Keycloak y BD")
    void testEliminarUsuario_DebeBorrarDeTodo() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        doNothing().when(keycloakUserService).deleteUser(anyString());
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        // When
        usuarioService.deleteUsuario(1L);

        // Then
        verify(keycloakUserService).deleteUser("keycloak-123");
        verify(usuarioRepository).delete(usuarioMock);
    }

    @Test
    @DisplayName("Eliminar usuario huérfano debe solo borrar de BD")
    void testEliminarUsuarioHuerfano() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.empty());
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        // When
        usuarioService.deleteUsuario(1L);

        // Then
        verify(keycloakUserService, never()).deleteUser(anyString());
        verify(usuarioRepository).delete(usuarioMock);
    }

    @Test
    @DisplayName("Eliminar usuario local solo debe verificar ausencia en Keycloak")
    void testEliminarUsuarioLocal() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.empty());
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        // When
        usuarioService.deleteUsuarioLocal(1L);

        // Then
        verify(usuarioRepository).delete(usuarioMock);
    }

    @Test
    @DisplayName("Eliminar usuario local debe fallar si existe en Keycloak")
    void testEliminarUsuarioLocal_DebeFallarSiExisteEnKeycloak() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));

        // When & Then
        assertThatThrownBy(() -> usuarioService.deleteUsuarioLocal(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SÍ existe en Keycloak");
    }

    // ==================== TESTS DE BÚSQUEDA ====================

    @Test
    @DisplayName("Buscar por ID debe retornar usuario")
    void testBuscarPorId() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // When
        Usuario result = usuarioService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Buscar por ID inexistente debe lanzar excepción")
    void testBuscarPorIdInexistente() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("Buscar por username debe retornar usuario")
    void testBuscarPorUsername() {
        // Given
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuarioMock));

        // When
        Usuario result = usuarioService.findByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Buscar por keycloakId debe retornar usuario")
    void testBuscarPorKeycloakId() {
        // Given
        when(usuarioRepository.findByKeycloakId("keycloak-123")).thenReturn(Optional.of(usuarioMock));

        // When
        Usuario result = usuarioService.findByKeycloakId("keycloak-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKeycloakId()).isEqualTo("keycloak-123");
    }

    @Test
    @DisplayName("Obtener todos los usuarios debe retornar lista")
    void testObtenerTodosLosUsuarios() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioMock);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = usuarioService.getAllUsuariosLocales();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    // ==================== TESTS DE EMPRESA ====================

    @Test
    @DisplayName("Obtener usuarios por empresa debe filtrar correctamente")
    void testObtenerUsuariosPorEmpresa() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioMock);
        when(usuarioRepository.findByEmpresaId(100L)).thenReturn(usuarios);

        // When
        List<Usuario> result = usuarioService.getUsuariosByEmpresaId(100L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpresaId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Obtener empresa por usuario debe retornar empresa")
    void testObtenerEmpresaPorUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(empresaRepository.findById(100L)).thenReturn(Optional.of(empresaMock));

        // When
        Empresa result = usuarioService.getEmpresaByUsuarioId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getRazonSocial()).isEqualTo("Empresa Test S.A.S.");
    }

    @Test
    @DisplayName("Obtener empresa de usuario sin empresa debe fallar")
    void testObtenerEmpresaSinEmpresa() {
        // Given
        usuarioMock.setEmpresaId(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // When & Then
        assertThatThrownBy(() -> usuarioService.getEmpresaByUsuarioId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no tiene una empresa asociada");
    }

    // ==================== TESTS DE HABILITACIÓN DE LOGIN ====================

    @Test
    @DisplayName("Habilitar login debe limpiar acciones requeridas")
    void testHabilitarLogin() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(keycloakUserService.getUserByUsername("testuser")).thenReturn(Optional.of(keycloakUserMock));
        // No hacer stub de clearRequiredActions - es void y no retorna nada
        // Mockito automáticamente no hace nada en métodos void

        // When
        usuarioService.habilitarLoginUsuario(1L);

        // Then
        verify(keycloakUserService).clearRequiredActions("keycloak-123");
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("Crear usuario con datos incompletos debe usar valores proporcionados")
    void testCrearUsuarioConDatosMinimos() {
        // Given
        when(keycloakUserService.createUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn("keycloak-new-id");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario result = usuarioService.createUsuarioCompleto(
                "minuser", "min@example.com", "pass123",
                "Min", "User", "CC", "123",
                LocalDate.of(2000, 1, 1), "M",
                "300", null, "Dir", "City", "000",
                "EMP", "USER"
        );

        // Then
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertThat(savedUsuario.getUsername()).isEqualTo("minuser");
        assertThat(savedUsuario.getEmail()).isEqualTo("min@example.com");
    }
}
