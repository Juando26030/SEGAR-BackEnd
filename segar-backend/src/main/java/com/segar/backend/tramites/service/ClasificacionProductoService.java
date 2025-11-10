package com.segar.backend.tramites.service;

import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO;
import com.segar.backend.tramites.domain.ClasificacionProducto;
import com.segar.backend.tramites.infrastructure.ClasificacionProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClasificacionProductoService {

    @Autowired
    private ClasificacionProductoRepository clasificacionRepository;

    public ClasificacionProducto guardarClasificacion(Long productoId, ClasificacionProductoDTO dto) {
        ClasificacionProducto clasificacion = ClasificacionProducto.builder()
                .productoId(productoId)
                .categoria(dto.getCategoria())
                .nivelRiesgo(ClasificacionProducto.NivelRiesgo.valueOf(dto.getNivelRiesgo().name()))
                .poblacionObjetivo(dto.getPoblacionObjetivo())
                .procesamiento(dto.getProcesamiento())
                .tipoAccion(ClasificacionProducto.TipoAccion.valueOf(dto.getTipoAccion().name()))
                .esImportado(dto.getEsImportado())
                .fechaClasificacion(LocalDateTime.now())
                .build();

        return clasificacionRepository.save(clasificacion);
    }

    public ClasificacionProducto obtenerClasificacionPorProducto(Long productoId) {
        return clasificacionRepository.findByProductoId(productoId).orElse(null);
    }

    public boolean existeClasificacion(Long productoId) {
        return clasificacionRepository.existsByProductoId(productoId);
    }

    public ClasificacionProducto actualizarClasificacion(Long productoId, ClasificacionProductoDTO dto) {
        ClasificacionProducto clasificacion = clasificacionRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Clasificación no encontrada"));

        clasificacion.setCategoria(dto.getCategoria());
        clasificacion.setNivelRiesgo(ClasificacionProducto.NivelRiesgo.valueOf(dto.getNivelRiesgo().name()));
        clasificacion.setPoblacionObjetivo(dto.getPoblacionObjetivo());
        clasificacion.setProcesamiento(dto.getProcesamiento());
        clasificacion.setTipoAccion(ClasificacionProducto.TipoAccion.valueOf(dto.getTipoAccion().name()));
        clasificacion.setEsImportado(dto.getEsImportado());
        clasificacion.setFechaClasificacion(LocalDateTime.now());

        return clasificacionRepository.save(clasificacion);
    }
}
