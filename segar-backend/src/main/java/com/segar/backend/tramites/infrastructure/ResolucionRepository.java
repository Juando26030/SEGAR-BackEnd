package com.segar.backend.tramites.infrastructure;


import com.segar.backend.tramites.domain.Resolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para resoluciones INVIMA
 */
@Repository
public interface ResolucionRepository extends JpaRepository<Resolucion, Long> {

    Optional<Resolucion> findByTramiteId(Long tramiteId);

    List<Resolucion> findByEstado(String estado);

    boolean existsByNumeroResolucion(String numeroResolucion);

    @Query("SELECT COUNT(r) FROM Resolucion r WHERE YEAR(r.fechaEmision) = :year")
    Long countByYear(@Param("year") int year);
}
