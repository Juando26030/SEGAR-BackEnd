package com.segar.backend.tramites.service;

import com.segar.backend.shared.domain.EstadoResolucion;
import com.segar.backend.shared.domain.EstadoSolicitud;
import com.segar.backend.tramites.api.dto.HistorialTramiteDTO;
import com.segar.backend.tramites.api.dto.RegistroSanitarioDTO;
import com.segar.backend.tramites.api.dto.ResolucionDTO;
import com.segar.backend.tramites.api.dto.TramiteCompletoDTO;
import com.segar.backend.tramites.domain.HistorialTramite;
import com.segar.backend.tramites.domain.RegistroSanitario;
import com.segar.backend.tramites.domain.Resolucion;
import com.segar.backend.tramites.domain.Solicitud;
import com.segar.backend.tramites.infrastructure.HistorialTramiteRepository;
import com.segar.backend.tramites.infrastructure.RegistroSanitarioRepository;
import com.segar.backend.tramites.infrastructure.ResolucionRepository;
import com.segar.backend.tramites.infrastructure.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de resoluciones y cumplimiento de trámites INVIMA
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResolucionService {

    private final ResolucionRepository resolucionRepository;
    private final RegistroSanitarioRepository registroSanitarioRepository;
    private final SolicitudRepository solicitudRepository;
    private final HistorialTramiteRepository historialTramiteRepository;
    private final HistorialTramiteServiceImpl historialTramiteService;

    /**
     * Obtener la resolución de un trámite
     */
    @Transactional(readOnly = true)
    public ResolucionDTO obtenerResolucion(Long tramiteId) {
        Resolucion resolucion = resolucionRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("Resolución no encontrada para el trámite: " + tramiteId));

        return mapearResolucionADTO(resolucion);
    }

    /**
     * Obtener el registro sanitario de un trámite (solo si está aprobado)
     */
    @Transactional(readOnly = true)
    public RegistroSanitarioDTO obtenerRegistroSanitario(Long tramiteId) {
        // Verificar que el trámite tenga una resolución aprobada
        Resolucion resolucion = resolucionRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("No se encontró resolución para el trámite: " + tramiteId));

        if (resolucion.getEstado() != EstadoResolucion.APROBADA) {
            throw new RuntimeException("El trámite no tiene una resolución aprobada");
        }

        RegistroSanitario registroSanitario = registroSanitarioRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("Registro sanitario no encontrado para el trámite: " + tramiteId));

        return mapearRegistroSanitarioADTO(registroSanitario);
    }

    /**
     * Obtener información completa del trámite con resolución, registro y historial
     */
    @Transactional(readOnly = true)
    public TramiteCompletoDTO obtenerTramiteCompleto(Long tramiteId) {
        // Obtener la solicitud
        Solicitud solicitud = solicitudRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado: " + tramiteId));

        // Obtener resolución si existe
        ResolucionDTO resolucionDTO = null;
        try {
            Resolucion resolucion = resolucionRepository.findByTramiteId(tramiteId).orElse(null);
            if (resolucion != null) {
                resolucionDTO = mapearResolucionADTO(resolucion);
            }
        } catch (Exception e) {
            // Si no hay resolución, continuamos
        }

        // Obtener registro sanitario si existe y está aprobado
        RegistroSanitarioDTO registroSanitarioDTO = null;
        try {
            if (resolucionDTO != null && "APROBADA".equals(resolucionDTO.getEstado())) {
                RegistroSanitario registroSanitario = registroSanitarioRepository.findByTramiteId(tramiteId).orElse(null);
                if (registroSanitario != null) {
                    registroSanitarioDTO = mapearRegistroSanitarioADTO(registroSanitario);
                }
            }
        } catch (Exception e) {
            // Si no hay registro, continuamos
        }

        // Obtener historial
        List<HistorialTramiteDTO> historial = obtenerHistorial(tramiteId);

        return TramiteCompletoDTO.builder()
                .id(solicitud.getId())
                .numeroRadicado(solicitud.getNumeroRadicado())
                .estado(solicitud.getEstado().name())
                .fechaCreacion(solicitud.getFechaRadicacion())
                .empresaId(solicitud.getEmpresaId())
                .productoId(solicitud.getProducto() != null ? solicitud.getProducto().getId() : null)
                .resolucion(resolucionDTO)
                .registroSanitario(registroSanitarioDTO)
                .historial(historial)
                .build();
    }

    /**
     * Obtener historial de un trámite
     */
    @Transactional(readOnly = true)
    public List<HistorialTramiteDTO> obtenerHistorial(Long tramiteId) {
        List<HistorialTramite> historial = historialTramiteRepository.findByTramiteIdOrderByFechaDesc(tramiteId);

        return historial.stream()
                .map(this::mapearHistorialADTO)
                .collect(Collectors.toList());
    }

    /**
     * Descargar documento de resolución
     */
    @Transactional(readOnly = true)
    public Resource descargarResolucion(Long tramiteId) {
        Resolucion resolucion = resolucionRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("Resolución no encontrada para el trámite: " + tramiteId));

        if (resolucion.getDocumentoUrl() == null || resolucion.getDocumentoUrl().isEmpty()) {
            throw new RuntimeException("La resolución no tiene documento asociado");
        }

        try {
            Path filePath = Paths.get(resolucion.getDocumentoUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo de resolución");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar la resolución: " + e.getMessage());
        }
    }

    /**
     * Descargar registro sanitario
     */
    @Transactional(readOnly = true)
    public Resource descargarRegistroSanitario(Long tramiteId) {
        RegistroSanitario registroSanitario = registroSanitarioRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("Registro sanitario no encontrado para el trámite: " + tramiteId));

        if (registroSanitario.getDocumentoUrl() == null || registroSanitario.getDocumentoUrl().isEmpty()) {
            throw new RuntimeException("El registro sanitario no tiene documento asociado");
        }

        try {
            Path filePath = Paths.get(registroSanitario.getDocumentoUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo del registro sanitario");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el registro sanitario: " + e.getMessage());
        }
    }

    /**
     * Finalizar un trámite
     */
    public void finalizarTramite(Long tramiteId) {
        Solicitud solicitud = solicitudRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado: " + tramiteId));

        // Verificar que el trámite tenga una resolución aprobada
        Resolucion resolucion = resolucionRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("El trámite no tiene resolución"));

        if (resolucion.getEstado() != EstadoResolucion.APROBADA) {
            throw new RuntimeException("Solo se pueden finalizar trámites con resolución aprobada");
        }

        // Verificar que exista el registro sanitario
        registroSanitarioRepository.findByTramiteId(tramiteId)
                .orElseThrow(() -> new RuntimeException("El trámite no tiene registro sanitario"));

        // Cambiar estado a FINALIZADA
        solicitud.setEstado(EstadoSolicitud.FINALIZADA);
        solicitudRepository.save(solicitud);

        // Registrar en historial
        historialTramiteService.registrarEvento(
                tramiteId,
                "FINALIZACION",
                "Trámite finalizado exitosamente con registro sanitario emitido",
                "Sistema",
                EstadoSolicitud.FINALIZADA.name()
        );
    }

    // Métodos privados de mapeo

    private ResolucionDTO mapearResolucionADTO(Resolucion resolucion) {
        return ResolucionDTO.builder()
                .id(resolucion.getId())
                .numeroResolucion(resolucion.getNumeroResolucion())
                .fechaEmision(resolucion.getFechaEmision())
                .autoridad(resolucion.getAutoridad())
                .estado(resolucion.getEstado().name())
                .observaciones(resolucion.getObservaciones())
                .tramiteId(resolucion.getTramiteId())
                .documentoUrl(resolucion.getDocumentoUrl())
                .fechaNotificacion(resolucion.getFechaNotificacion())
                .build();
    }

    private RegistroSanitarioDTO mapearRegistroSanitarioADTO(RegistroSanitario registroSanitario) {
        return RegistroSanitarioDTO.builder()
                .id(registroSanitario.getId())
                .numeroRegistro(registroSanitario.getNumeroRegistro())
                .fechaExpedicion(registroSanitario.getFechaExpedicion())
                .fechaVencimiento(registroSanitario.getFechaVencimiento())
                .productoId(registroSanitario.getProductoId())
                .empresaId(registroSanitario.getEmpresaId())
                .estado(registroSanitario.getEstado().name())
                .resolucionId(registroSanitario.getResolucionId())
                .documentoUrl(registroSanitario.getDocumentoUrl())
                .build();
    }

    private HistorialTramiteDTO mapearHistorialADTO(HistorialTramite historial) {
        return HistorialTramiteDTO.builder()
                .id(historial.getId())
                .tramiteId(historial.getTramiteId())
                .fecha(historial.getFecha())
                .accion(historial.getAccion())
                .descripcion(historial.getDescripcion())
                .usuario(historial.getUsuario())
                .estado(historial.getEstado())
                .build();
    }
}
