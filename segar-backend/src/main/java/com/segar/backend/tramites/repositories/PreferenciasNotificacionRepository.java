package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.PreferenciasNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciasNotificacionRepository extends JpaRepository<PreferenciasNotificacion, Long> {
    Optional<PreferenciasNotificacion> findByTramiteId(Long tramiteId);
}
