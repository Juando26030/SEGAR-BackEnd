package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.PreferenciasNotificacion;

import java.util.Optional;

public interface PreferenciasNotificacionRepository extends JpaRepository<PreferenciasNotificacion, Long> {
    Optional<PreferenciasNotificacion> findByTramiteId(Long tramiteId);
}
