package com.segar.backend.calendario.infrastructure;

import com.segar.backend.calendario.domain.EstadoEvento;
import com.segar.backend.calendario.domain.Evento;
import com.segar.backend.calendario.domain.PrioridadEvento;
import com.segar.backend.calendario.domain.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query("SELECT e FROM Evento e WHERE YEAR(e.fecha) = :anio AND MONTH(e.fecha) = :mes")
    List<Evento> findByMesAndAnio(@Param("mes") int mes, @Param("anio") int anio);

    // NUEVO: Filtros por empresa
    @Query("SELECT e FROM Evento e WHERE e.empresaId = :empresaId AND YEAR(e.fecha) = :anio AND MONTH(e.fecha) = :mes")
    List<Evento> findByEmpresaIdAndMesAndAnio(@Param("empresaId") Long empresaId, @Param("mes") int mes, @Param("anio") int anio);

    // NUEVO: Filtros por usuario
    @Query("SELECT e FROM Evento e WHERE e.usuarioId = :usuarioId AND YEAR(e.fecha) = :anio AND MONTH(e.fecha) = :mes")
    List<Evento> findByUsuarioIdAndMesAndAnio(@Param("usuarioId") Long usuarioId, @Param("mes") int mes, @Param("anio") int anio);

    List<Evento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByTipo(TipoEvento tipo);

    List<Evento> findByPrioridad(PrioridadEvento prioridad);

    List<Evento> findByEmpresaId(Long empresaId);

    // NUEVO: Buscar por usuario
    List<Evento> findByUsuarioId(Long usuarioId);

    List<Evento> findByTramiteId(Long tramiteId);

    // Estadísticas por empresa
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.empresaId = :empresaId AND e.estado = :estado")
    long countByEmpresaIdAndEstado(@Param("empresaId") Long empresaId, @Param("estado") EstadoEvento estado);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.empresaId = :empresaId AND e.prioridad = 'ALTA' AND e.estado = 'ACTIVO'")
    long countEventosCriticosByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.empresaId = :empresaId AND e.fecha < CURRENT_DATE AND e.estado = 'ACTIVO'")
    long countEventosVencidosByEmpresaId(@Param("empresaId") Long empresaId);

    // NUEVO: Estadísticas por usuario
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.usuarioId = :usuarioId AND e.estado = :estado")
    long countByUsuarioIdAndEstado(@Param("usuarioId") Long usuarioId, @Param("estado") EstadoEvento estado);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.usuarioId = :usuarioId AND e.prioridad = 'ALTA' AND e.estado = 'ACTIVO'")
    long countEventosCriticosByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.usuarioId = :usuarioId AND e.fecha < CURRENT_DATE AND e.estado = 'ACTIVO'")
    long countEventosVencidosByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Estadísticas globales (las existentes)
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.estado = :estado")
    long countByEstado(@Param("estado") EstadoEvento estado);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.prioridad = 'ALTA' AND e.estado = 'ACTIVO'")
    long countEventosCriticos();

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.fecha < CURRENT_DATE AND e.estado = 'ACTIVO'")
    long countEventosVencidos();

    // Próximos eventos por empresa
    @Query("SELECT e FROM Evento e WHERE e.empresaId = :empresaId AND e.fecha >= CURRENT_DATE AND e.estado = 'ACTIVO' ORDER BY e.fecha ASC")
    List<Evento> findTop3ProximosEventosByEmpresaId(@Param("empresaId") Long empresaId, org.springframework.data.domain.Pageable pageable);

    // NUEVO: Próximos eventos por usuario
    @Query("SELECT e FROM Evento e WHERE e.usuarioId = :usuarioId AND e.fecha >= CURRENT_DATE AND e.estado = 'ACTIVO' ORDER BY e.fecha ASC")
    List<Evento> findTop3ProximosEventosByUsuarioId(@Param("usuarioId") Long usuarioId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.fecha >= CURRENT_DATE AND e.estado = 'ACTIVO' ORDER BY e.fecha ASC")
    List<Evento> findTop3ProximosEventos(org.springframework.data.domain.Pageable pageable);
}

