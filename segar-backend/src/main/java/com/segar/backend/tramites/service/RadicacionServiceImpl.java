package com.segar.backend.tramites.service;

import com.segar.backend.shared.domain.EstadoPago;
import com.segar.backend.shared.domain.EstadoSolicitud;
import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.shared.domain.Producto;
import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.documentos.infrastructure.DocumentoRepository;
import com.segar.backend.tramites.api.dto.RadicacionSolicitudDTO;
import com.segar.backend.tramites.api.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.tramites.domain.Pago;
import com.segar.backend.tramites.domain.Solicitud;
import com.segar.backend.tramites.domain.exceptions.DocumentosIncompletosException;
import com.segar.backend.tramites.domain.exceptions.PagoInvalidoException;
import com.segar.backend.tramites.domain.exceptions.SolicitudDuplicadaException;
import com.segar.backend.tramites.infrastructure.PagoRepository;
import com.segar.backend.shared.infrastructure.ProductoRepository;
import com.segar.backend.tramites.infrastructure.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final DocumentoRepository documentoRepository;

    public SolicitudRadicadaResponseDTO radicarSolicitud(RadicacionSolicitudDTO radicacionDTO) {
        // Validaciones previas
        validarEmpresaRegistrada(radicacionDTO.getEmpresaId());
        validarDocumentosObligatorios(radicacionDTO.getDocumentosId());
        validarPagoAprobado(radicacionDTO.getPagoId());
        validarSolicitudNoExistente(radicacionDTO);

        // Obtener entidades relacionadas
        Producto producto = productoRepository.findById(radicacionDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Pago pago = pagoRepository.findById(radicacionDTO.getPagoId())
            .orElseThrow(() -> new PagoInvalidoException("Pago no encontrado"));

        List<Documento> documentos = documentoRepository.findAllById(radicacionDTO.getDocumentosId());

        // Crear y configurar la solicitud
        Solicitud solicitud = Solicitud.builder()
            .empresaId(radicacionDTO.getEmpresaId())
            .producto(producto)
            .tipoTramite(radicacionDTO.getTipoTramite())
            .estado(EstadoSolicitud.RADICADA)
            .numeroRadicado(generarNumeroRadicado())
            .fechaRadicacion(LocalDateTime.now())
            .observaciones(radicacionDTO.getObservaciones())
            .pago(pago)
            .documentos(documentos)
            .build();

        // Actualizar documentos para referenciar la solicitud
        documentos.forEach(doc -> doc.setSolicitud(solicitud));

        // Guardar en base de datos
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        // Crear respuesta
        return SolicitudRadicadaResponseDTO.builder()
            .numeroRadicado(solicitudGuardada.getNumeroRadicado())
            .estado(solicitudGuardada.getEstado().name())
            .fechaRadicacion(solicitudGuardada.getFechaRadicacion())
            .empresaId(solicitudGuardada.getEmpresaId())
            .productoId(solicitudGuardada.getProducto().getId())
            .tipoTramite(solicitudGuardada.getTipoTramite().name())
            .mensaje("Solicitud radicada exitosamente ante INVIMA")
            .build();
    }


    public List<Solicitud> obtenerSolicitudesRadicadas(Long empresaId) {
        return solicitudRepository.findByEmpresaIdAndEstado(empresaId, EstadoSolicitud.RADICADA);
    }


    public Map<String, Object> validarPreRequisitos(Long empresaId) {
        Map<String, Object> validaciones = new HashMap<>();

        try {
            // Validar empresa registrada
            validaciones.put("empresaRegistrada", validarEmpresaExiste(empresaId));

            // Validar documentos disponibles
            List<Documento> documentos = documentoRepository.findByEmpresaId(empresaId);
            validaciones.put("documentosCargados", !documentos.isEmpty());
            validaciones.put("cantidadDocumentos", documentos.size());

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

        List<Documento> documentos = documentoRepository.findAllById(documentosId);
        if (documentos.size() != documentosId.size()) {
            throw new DocumentosIncompletosException("Algunos documentos especificados no existen");
        }

        // Validar que los documentos estén completos
        for (Documento doc : documentos) {
            if (doc.getRutaArchivo() == null || doc.getRutaArchivo().isEmpty()) {
                throw new DocumentosIncompletosException("Documento sin ruta de archivo: " + doc.getTipoDocumento());
            }
            if (doc.getNombreArchivo() == null || doc.getNombreArchivo().isEmpty()) {
                throw new DocumentosIncompletosException("Documento sin nombre de archivo: " + doc.getTipoDocumento());
            }
        }
    }

    private void validarPagoAprobado(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
            .orElseThrow(() -> new PagoInvalidoException("Pago no encontrado"));

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new PagoInvalidoException("El pago debe estar en estado APROBADO para poder radicar");
        }
    }

    private void validarSolicitudNoExistente(RadicacionSolicitudDTO radicacionDTO) {
        Optional<Solicitud> solicitudExistente = solicitudRepository
            .findByEmpresaIdAndProductoIdAndTipoTramite(
                radicacionDTO.getEmpresaId(),
                radicacionDTO.getProductoId(),
                radicacionDTO.getTipoTramite()
            );

        if (solicitudExistente.isPresent()) {
            Solicitud existente = solicitudExistente.get();
            if (existente.getEstado() == EstadoSolicitud.RADICADA || 
                existente.getEstado() == EstadoSolicitud.APROBADA) {
                throw new SolicitudDuplicadaException(
                    "Ya existe una solicitud " + existente.getEstado().name() +
                    " para este producto y tipo de trámite"
                );
            }
        }
    }
}
