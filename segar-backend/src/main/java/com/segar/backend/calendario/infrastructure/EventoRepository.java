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

    List<Evento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByTipo(TipoEvento tipo);

    List<Evento> findByPrioridad(PrioridadEvento prioridad);

    List<Evento> findByEmpresaId(Long empresaId);

    List<Evento> findByTramiteId(Long tramiteId);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.estado = :estado")
    long countByEstado(@Param("estado") EstadoEvento estado);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.prioridad = 'ALTA' AND e.estado = 'ACTIVO'")
    long countEventosCriticos();

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.fecha < CURRENT_DATE AND e.estado = 'ACTIVO'")
    long countEventosVencidos();

    @Query("SELECT e FROM Evento e WHERE e.fecha >= CURRENT_DATE AND e.estado = 'ACTIVO' ORDER BY e.fecha ASC")
    List<Evento> findTop3ProximosEventos(org.springframework.data.domain.Pageable pageable);

}
