package com.segar.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.segar.backend.dto.RadicacionSolicitudDTO;
import com.segar.backend.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.exceptions.DocumentosIncompletosException;
import com.segar.backend.exceptions.PagoInvalidoException;
import com.segar.backend.exceptions.SolicitudDuplicadaException;
import com.segar.backend.models.*;
import com.segar.backend.repositories.DocumentoRepository;
import com.segar.backend.repositories.PagoRepository;
import com.segar.backend.repositories.ProductoRepository;
import com.segar.backend.repositories.SolicitudRepository;
import com.segar.backend.services.implementations.SolicitudServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de radicación de solicitudes
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    private RadicacionSolicitudDTO radicacionDTO;
    private Producto producto;
    private Pago pago;
    private List<Documento> documentos;

    @BeforeEach
    void setUp() {
        // Configurar producto de prueba
        producto = Producto.builder()
            .id(1L)
            .nombre("Yogurt Natural")
            .descripcion("Yogurt natural sin azúcar")
            .build();

        // Configurar pago de prueba
        pago = Pago.builder()
            .id(1L)
            .monto(new BigDecimal("1250000.00"))
            .metodoPago(MetodoPago.TARJETA_CREDITO)
            .estado(EstadoPago.APROBADO)
            .referenciaPago("PAY-2024-001")
            .build();

        // Configurar documentos de prueba (todos los obligatorios)
        documentos = Arrays.asList(
            crearDocumento(1L, TipoDocumento.CERTIFICADO_CONSTITUCION),
            crearDocumento(2L, TipoDocumento.RUT),
            crearDocumento(3L, TipoDocumento.CONCEPTO_SANITARIO),
            crearDocumento(4L, TipoDocumento.FICHA_TECNICA),
            crearDocumento(5L, TipoDocumento.ETIQUETA),
            crearDocumento(6L, TipoDocumento.ANALISIS_MICROBIOLOGICO),
            crearDocumento(7L, TipoDocumento.CERTIFICADO_BPM)
        );

        // Configurar DTO de radicación
        radicacionDTO = RadicacionSolicitudDTO.builder()
            .empresaId(1001L)
            .productoId(1L)
            .tipoTramite(TipoTramite.REGISTRO)
            .documentosId(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L))
            .pagoId(1L)
            .observaciones("Solicitud de prueba")
            .build();
    }

    @Test
    void testRadicarSolicitudExitosa() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(false);
        when(documentoRepository.findAllById(anyList())).thenReturn(documentos);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(solicitudRepository.existsByNumeroRadicado(anyString())).thenReturn(false);
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud solicitud = invocation.getArgument(0);
            solicitud.setId(1L);
            return solicitud;
        });

        // Act
        SolicitudRadicadaResponseDTO resultado = solicitudService.radicarSolicitud(radicacionDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNotNull(resultado.getNumeroRadicado());
        assertTrue(resultado.getNumeroRadicado().startsWith("INV-"));
        assertEquals(1001L, resultado.getEmpresaId());
        assertEquals("Yogurt Natural", resultado.getNombreProducto());
        assertEquals(TipoTramite.REGISTRO, resultado.getTipoTramite());
        assertEquals(EstadoSolicitud.RADICADA, resultado.getEstado());
        assertNotNull(resultado.getFechaRadicacion());
        assertEquals("Solicitud de prueba", resultado.getObservaciones());
        assertTrue(resultado.getMensaje().contains("radicada exitosamente"));

        // Verificar que se llamaron los métodos necesarios
        verify(solicitudRepository).save(any(Solicitud.class));
        verify(documentoRepository).saveAll(anyList());
    }

    @Test
    void testRadicarSolicitudProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void testRadicarSolicitudDuplicada() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(true);

        // Act & Assert
        SolicitudDuplicadaException exception = assertThrows(SolicitudDuplicadaException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("Ya existe una solicitud radicada"));
    }

    @Test
    void testRadicarSolicitudSinDocumentos() {
        // Arrange
        radicacionDTO.setDocumentosId(null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(false);

        // Act & Assert
        DocumentosIncompletosException exception = assertThrows(DocumentosIncompletosException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("No se han proporcionado documentos"));
    }

    @Test
    void testRadicarSolicitudDocumentosIncompletos() {
        // Arrange - Solo algunos documentos (faltan obligatorios)
        List<Documento> documentosIncompletos = Arrays.asList(
            crearDocumento(1L, TipoDocumento.CERTIFICADO_CONSTITUCION),
            crearDocumento(2L, TipoDocumento.RUT)
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(false);
        when(documentoRepository.findAllById(anyList())).thenReturn(documentosIncompletos);

        // Act & Assert
        DocumentosIncompletosException exception = assertThrows(DocumentosIncompletosException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("Falta el documento obligatorio"));
    }

    @Test
    void testRadicarSolicitudPagoNoExiste() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(false);
        when(documentoRepository.findAllById(anyList())).thenReturn(documentos);
        when(pagoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("No se encontró el pago"));
    }

    @Test
    void testRadicarSolicitudPagoNoAprobado() {
        // Arrange
        pago.setEstado(EstadoPago.PENDIENTE);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
            1L, TipoTramite.REGISTRO, EstadoSolicitud.RADICADA)).thenReturn(false);
        when(documentoRepository.findAllById(anyList())).thenReturn(documentos);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // Act & Assert
        PagoInvalidoException exception = assertThrows(PagoInvalidoException.class,
            () -> solicitudService.radicarSolicitud(radicacionDTO));

        assertTrue(exception.getMessage().contains("debe estar en estado APROBADO"));
    }

    private Documento crearDocumento(Long id, TipoDocumento tipo) {
        return Documento.builder()
            .id(id)
            .nombreArchivo("documento_" + tipo.name().toLowerCase() + ".pdf")
            .tipoDocumento(tipo)
            .rutaArchivo("/uploads/docs/doc_" + id + ".pdf")
            .fechaCarga(LocalDateTime.now())
            .obligatorio(true)
            .build();
    }
}
