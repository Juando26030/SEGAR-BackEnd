package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByTramiteIdOrderByDateDesc(Long tramiteId);
}
