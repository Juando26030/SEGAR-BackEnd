package com.segar.backend.tramites.service;


import com.segar.backend.tramites.api.dto.ResolucionDTO;
import com.segar.backend.tramites.domain.Resolucion;
import com.segar.backend.tramites.infrastructure.ResolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de resoluciones
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResolucionServiceImpl {

    private final ResolucionRepository resolucionRepository;

     
    public ResolucionDTO generarResolucion(Long tramiteId, String decision, String observaciones, String autoridad) {
        // Crear resolución
        Resolucion resolucion = Resolucion.builder()
            .numeroResolucion(generarNumeroResolucion())
            .fechaEmision(LocalDateTime.now())
            .autoridad(autoridad != null ? autoridad : "INVIMA")
            .estado(decision.equals("APROBAR") ?
                com.segar.backend.shared.domain.EstadoResolucion.APROBADA :
                com.segar.backend.shared.domain.EstadoResolucion.RECHAZADA)
            .observaciones(observaciones)
            .tramiteId(tramiteId)
            .fechaNotificacion(LocalDateTime.now())
            .build();

        Resolucion resolucionGuardada = resolucionRepository.save(resolucion);

        return mapearADTO(resolucionGuardada);
    }

     
    public ResolucionDTO obtenerResolucionPorTramite(Long tramiteId) {
        return resolucionRepository.findByTramiteId(tramiteId)
            .map(this::mapearADTO)
            .orElse(null);
    }

     
    public String generarNumeroResolucion() {
        int year = LocalDateTime.now().getYear();
        // Usar count() simple en lugar de countByYear() que no existe
        Long count = resolucionRepository.count() + 1;
        return String.format("%d-INVIMA-%04d", year, count);
    }

     
    public void notificarResolucion(Long resolucionId) {
        // Implementar lógica de notificación
        // Por ahora, actualizar fecha de notificación
        resolucionRepository.findById(resolucionId).ifPresent(resolucion -> {
            resolucion.setFechaNotificacion(LocalDateTime.now());
            resolucionRepository.save(resolucion);
        });
    }

    private ResolucionDTO mapearADTO(Resolucion resolucion) {
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
}
