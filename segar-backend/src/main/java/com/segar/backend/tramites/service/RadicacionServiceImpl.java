package com.segar.backend.tramites.service;

import com.segar.backend.shared.domain.EstadoPago;
import com.segar.backend.shared.domain.EstadoSolicitud;
import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.events.DocumentValidationRequestEvent;
import com.segar.backend.shared.events.DocumentValidationResponseEvent;
import com.segar.backend.shared.events.DocumentosSolicitudAsociadosEvent;
import com.segar.backend.shared.infrastructure.ProductoRepository;



import com.segar.backend.tramites.api.dto.RadicacionSolicitudDTO;
import com.segar.backend.tramites.api.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.tramites.domain.Pago;
import com.segar.backend.tramites.domain.Solicitud;
import com.segar.backend.tramites.domain.exceptions.DocumentosIncompletosException;
import com.segar.backend.tramites.domain.exceptions.PagoInvalidoException;
import com.segar.backend.tramites.domain.exceptions.SolicitudDuplicadaException;
import com.segar.backend.tramites.infrastructure.PagoRepository;
import com.segar.backend.tramites.infrastructure.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del servicio para el Paso 5: Radicación de la Solicitud
 *
 * Maneja todo el proceso de radicación formal ante INVIMA con validaciones
 * previas, generación de radicado y persistencia en base de datos.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RadicacionServiceImpl {

    private final SolicitudRepository solicitudRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private final ApplicationEventPublisher eventPublisher;
    // Map para almacenar respuestas de validaciones asíncronas
    private final Map<String, DocumentValidationResponseEvent> validationResponses = new ConcurrentHashMap<>();


    
    public List<Solicitud> obtenerSolicitudesRadicadas(Long empresaId) {
        return solicitudRepository.findByEmpresaIdAndEstado(empresaId, EstadoSolicitud.RADICADA);
    }


    public Map<String, Object> validarPreRequisitos(Long empresaId) {
        Map<String, Object> validaciones = new HashMap<>();

        try {
            // Validar empresa registrada
            validaciones.put("empresaRegistrada", validarEmpresaExiste(empresaId));

            // Solicitar validación de documentos por eventos
            String requestId = UUID.randomUUID().toString();
            eventPublisher.publishEvent(new DocumentValidationRequestEvent(
                    requestId,
                    empresaId,
                    null,
                    "EMPRESA"
            ));

            // Esperar respuesta (con timeout)
            DocumentValidationResponseEvent response = waitForValidationResponse(requestId, 5000);

            if (response != null) {
                validaciones.put("documentosCargados", response.documentCount() > 0);
                validaciones.put("cantidadDocumentos", response.documentCount());
            } else {
                validaciones.put("documentosCargados", false);
                validaciones.put("cantidadDocumentos", 0);
                validaciones.put("documentosError", "Timeout validando documentos");
            }

            // Validar pagos aprobados
            List<Pago> pagosAprobados = pagoRepository.findByEmpresaIdAndEstado(empresaId, EstadoPago.APROBADO);
            validaciones.put("pagosAprobados", !pagosAprobados.isEmpty());
            validaciones.put("cantidadPagos", pagosAprobados.size());

            // Estado general
            boolean puedeRadicar = (boolean) validaciones.get("empresaRegistrada") &&
                                  (boolean) validaciones.get("documentosCargados") &&
                                  (boolean) validaciones.get("pagosAprobados");

            validaciones.put("puedeRadicar", puedeRadicar);
            validaciones.put("mensaje", puedeRadicar ?
                "Todos los requisitos cumplidos para radicación" :
                "Faltan requisitos para poder radicar");

        } catch (Exception e) {
            validaciones.put("error", e.getMessage());
            validaciones.put("puedeRadicar", false);
        }

        return validaciones;
    }


    public Solicitud buscarPorNumeroRadicado(String numeroRadicado) {
        return solicitudRepository.findByNumeroRadicado(numeroRadicado)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con radicado: " + numeroRadicado));
    }


    public String generarNumeroRadicado() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = solicitudRepository.count() + 1;
        return String.format("INV-%s-%06d", timestamp, count);
    }

    // Métodos privados de validación
    private void validarEmpresaRegistrada(Long empresaId) {
        if (!validarEmpresaExiste(empresaId)) {
            throw new RuntimeException("Empresa no registrada en el sistema");
        }
    }

    private boolean validarEmpresaExiste(Long empresaId) {
        // Validamos que el ID no sea nulo y sea positivo
        return empresaId != null && empresaId > 0;
    }

    private void validarDocumentosObligatorios(List<Long> documentosId) {
        if (documentosId == null || documentosId.isEmpty()) {
            throw new DocumentosIncompletosException("No se han cargado documentos obligatorios");
        }


        // Solicitar validación por eventos
        String requestId = UUID.randomUUID().toString();
        eventPublisher.publishEvent(new DocumentValidationRequestEvent(
                requestId,
                null,
                documentosId,
                "OBLIGATORIOS"
        ));

        // Esperar respuesta
        DocumentValidationResponseEvent response = waitForValidationResponse(requestId, 10000);

        if (response == null) {
            throw new DocumentosIncompletosException("Timeout validando documentos obligatorios");
        }

        if (!response.isValid()) {
            throw new DocumentosIncompletosException(response.errorMessage());
        }
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

    private void validarPagoAprobado(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
            .orElseThrow(() -> new PagoInvalidoException("Pago no encontrado"));

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new PagoInvalidoException("El pago debe estar en estado APROBADO para poder radicar");
        }
    }

}
