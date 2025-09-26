package com.segar.backend.tramites.service;

import com.segar.backend.shared.domain.*;


import com.segar.backend.shared.events.DocumentValidationResponseEvent;
import com.segar.backend.shared.events.DocumentosTipoValidationRequestEvent;
import com.segar.backend.shared.infrastructure.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.segar.backend.tramites.api.dto.RadicacionSolicitudDTO;
import com.segar.backend.tramites.api.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.tramites.domain.exceptions.DocumentosIncompletosException;
import com.segar.backend.tramites.domain.exceptions.PagoInvalidoException;
import com.segar.backend.tramites.domain.exceptions.SolicitudDuplicadaException;
import com.segar.backend.tramites.domain.*;
import com.segar.backend.tramites.infrastructure.PagoRepository;
import com.segar.backend.shared.events.DocumentosSolicitudUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;

import com.segar.backend.tramites.infrastructure.SolicitudRepository;



import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del servicio para gestión de solicitudes de trámites INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Service
@Transactional
public class SolicitudServiceImpl {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    // Map para almacenar respuestas de validaciones asíncronas
    private final Map<String, DocumentValidationResponseEvent> validationResponses = new ConcurrentHashMap<>();


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

        // Solicitar validación de documentos obligatorios por evento
        String requestId = UUID.randomUUID().toString();
        eventPublisher.publishEvent(new DocumentosTipoValidationRequestEvent(
                requestId,
                documentosId,
                DOCUMENTOS_OBLIGATORIOS_REGISTRO
        ));

        // Esperar respuesta con timeout
        DocumentValidationResponseEvent response = waitForValidationResponse(requestId, 10000);

        if (response == null) {
            throw new DocumentosIncompletosException("Timeout validando documentos obligatorios");
        }

        if (!response.isValid()) {
            throw new DocumentosIncompletosException(response.errorMessage());
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

    @EventListener
    public void handleDocumentValidationResponse(DocumentValidationResponseEvent event) {
        validationResponses.put(event.requestId(), event);
    }

    private DocumentValidationResponseEvent waitForValidationResponse(String requestId, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            DocumentValidationResponseEvent response = validationResponses.remove(requestId);
            if (response != null) {
                return response;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
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


    public List<Solicitud> obtenerSolicitudesPorEmpresa(Long empresaId) {
        return solicitudRepository.findByEmpresaId(empresaId);
    }


    public List<Solicitud> obtenerSolicitudesPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }


    public Solicitud buscarPorNumeroRadicado(String numeroRadicado) {
        return solicitudRepository.findByNumeroRadicado(numeroRadicado)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con número de radicado: " + numeroRadicado));
    }


    public Solicitud obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
    }
}
