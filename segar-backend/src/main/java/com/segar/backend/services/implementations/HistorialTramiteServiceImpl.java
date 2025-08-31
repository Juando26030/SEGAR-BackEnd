package com.segar.backend.services.implementations;

import com.segar.backend.dto.HistorialTramiteDTO;
import com.segar.backend.models.HistorialTramite;
import com.segar.backend.repositories.HistorialTramiteRepository;
import com.segar.backend.services.interfaces.HistorialTramiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de historial de trámites
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HistorialTramiteServiceImpl implements HistorialTramiteService {

    private final HistorialTramiteRepository historialTramiteRepository;

    @Override
    public void registrarEvento(Long tramiteId, String accion, String descripcion, String usuario, String estado) {
        HistorialTramite historial = HistorialTramite.builder()
            .tramiteId(tramiteId)
            .fecha(LocalDateTime.now())
            .accion(accion)
            .descripcion(descripcion)
            .usuario(usuario != null ? usuario : "Sistema")
            .estado(estado)
            .build();

        historialTramiteRepository.save(historial);
    }

    @Override
    public List<HistorialTramiteDTO> obtenerHistorialPorTramite(Long tramiteId) {
        return historialTramiteRepository.findByTramiteIdOrderByFechaDesc(tramiteId)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    private HistorialTramiteDTO mapearADTO(HistorialTramite historial) {
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
