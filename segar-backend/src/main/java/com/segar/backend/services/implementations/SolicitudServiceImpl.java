package com.segar.backend.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.segar.backend.services.interfaces.SolicitudService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestión de solicitudes de trámites INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Service
@Transactional
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Documentos obligatorios para registro sanitario
     */
    private static final List<TipoDocumento> DOCUMENTOS_OBLIGATORIOS_REGISTRO = Arrays.asList(
        TipoDocumento.CERTIFICADO_CONSTITUCION,
        TipoDocumento.RUT,
        TipoDocumento.CONCEPTO_SANITARIO,
        TipoDocumento.FICHA_TECNICA,
        TipoDocumento.ETIQUETA,
        TipoDocumento.ANALISIS_MICROBIOLOGICO,
        TipoDocumento.CERTIFICADO_BPM
    );

    @Override
    public SolicitudRadicadaResponseDTO radicarSolicitud(RadicacionSolicitudDTO radicacionDTO) {
        // Validar que el producto existe
        Producto producto = productoRepository.findById(radicacionDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + radicacionDTO.getProductoId()));

        // Validar que no existe solicitud duplicada
        validarSolicitudDuplicada(radicacionDTO.getProductoId(), radicacionDTO.getTipoTramite());

        // Validar documentos obligatorios
        validarDocumentosObligatorios(radicacionDTO.getDocumentosId());

        // Validar pago
        Pago pago = validarPago(radicacionDTO.getPagoId());

        // Crear y guardar la solicitud
        Solicitud solicitud = crearSolicitud(radicacionDTO, producto, pago);

        // Asociar documentos a la solicitud
        asociarDocumentos(solicitud, radicacionDTO.getDocumentosId());

        solicitud = solicitudRepository.save(solicitud);

        // Crear respuesta DTO
        return SolicitudRadicadaResponseDTO.builder()
            .numeroRadicado(solicitud.getNumeroRadicado())
            .estado(solicitud.getEstado().name())
            .fechaRadicacion(solicitud.getFechaRadicacion())
            .empresaId(solicitud.getEmpresaId())
            .productoId(solicitud.getProducto().getId())
            .tipoTramite(solicitud.getTipoTramite().name())
            .mensaje("Solicitud radicada exitosamente con número: " + solicitud.getNumeroRadicado())
            .build();
    }

    private void validarSolicitudDuplicada(Long productoId, TipoTramite tipoTramite) {
        if (solicitudRepository.existsByProductoIdAndTipoTramiteAndEstado(
                productoId, tipoTramite, EstadoSolicitud.RADICADA)) {
            throw new SolicitudDuplicadaException(
                "Ya existe una solicitud radicada para este producto y tipo de trámite");
        }
    }

    private void validarDocumentosObligatorios(List<Long> documentosId) {
        if (documentosId == null || documentosId.isEmpty()) {
            throw new DocumentosIncompletosException("No se han proporcionado documentos");
        }

        // Obtener documentos por IDs
        List<Documento> documentos = documentoRepository.findAllById(documentosId);

        if (documentos.size() != documentosId.size()) {
            throw new DocumentosIncompletosException("Algunos documentos no existen en el sistema");
        }

        // Verificar que están todos los documentos obligatorios
        List<TipoDocumento> tiposPresentes = documentos.stream()
            .map(Documento::getTipoDocumento)
            .toList();

        for (TipoDocumento tipoObligatorio : DOCUMENTOS_OBLIGATORIOS_REGISTRO) {
            if (!tiposPresentes.contains(tipoObligatorio)) {
                throw new DocumentosIncompletosException(
                    "Falta el documento obligatorio: " + tipoObligatorio.getDescripcion());
            }
        }
    }

    private Pago validarPago(Long pagoId) {
        Optional<Pago> pagoOpt = pagoRepository.findById(pagoId);

        if (pagoOpt.isEmpty()) {
            throw new PagoInvalidoException("No se encontró el pago con ID: " + pagoId);
        }

        Pago pago = pagoOpt.get();

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new PagoInvalidoException("El pago debe estar en estado APROBADO para radicar la solicitud");
        }

        return pago;
    }

    private Solicitud crearSolicitud(RadicacionSolicitudDTO radicacionDTO, Producto producto, Pago pago) {
        String numeroRadicado = generarNumeroRadicado();
        LocalDateTime fechaRadicacion = LocalDateTime.now();

        return Solicitud.builder()
            .empresaId(radicacionDTO.getEmpresaId())
            .producto(producto)
            .tipoTramite(radicacionDTO.getTipoTramite())
            .estado(EstadoSolicitud.RADICADA)
            .numeroRadicado(numeroRadicado)
            .fechaRadicacion(fechaRadicacion)
            .observaciones(radicacionDTO.getObservaciones())
            .pago(pago)
            .build();
    }

    private void asociarDocumentos(Solicitud solicitud, List<Long> documentosId) {
        List<Documento> documentos = documentoRepository.findAllById(documentosId);
        for (Documento documento : documentos) {
            documento.setSolicitud(solicitud);
        }
        documentoRepository.saveAll(documentos);
    }

    /**
     * Genera un número de radicado único con formato INV-{timestamp}
     */
    private String generarNumeroRadicado() {
        String numeroRadicado;
        do {
            long timestamp = System.currentTimeMillis();
            numeroRadicado = "INV-" + timestamp;
        } while (solicitudRepository.existsByNumeroRadicado(numeroRadicado));

        return numeroRadicado;
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorEmpresa(Long empresaId) {
        return solicitudRepository.findByEmpresaId(empresaId);
    }

    @Override
    public List<Solicitud> obtenerSolicitudesPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }

    @Override
    public Solicitud buscarPorNumeroRadicado(String numeroRadicado) {
        return solicitudRepository.findByNumeroRadicado(numeroRadicado)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con número de radicado: " + numeroRadicado));
    }

    @Override
    public Solicitud obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
    }
}
