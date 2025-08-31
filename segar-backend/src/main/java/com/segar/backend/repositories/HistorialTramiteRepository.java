package com.segar.backend.repositories;

import com.segar.backend.models.HistorialTramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para historial de trámites
 */
@Repository
public interface HistorialTramiteRepository extends JpaRepository<HistorialTramite, Long> {

    List<HistorialTramite> findByTramiteIdOrderByFechaDesc(Long tramiteId);

    List<HistorialTramite> findByTramiteIdAndEstado(Long tramiteId, String estado);

    @Query("SELECT h FROM HistorialTramite h WHERE h.tramiteId = :tramiteId AND h.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fecha DESC")
    List<HistorialTramite> findByTramiteIdAndFechaBetween(
        @Param("tramiteId") Long tramiteId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
