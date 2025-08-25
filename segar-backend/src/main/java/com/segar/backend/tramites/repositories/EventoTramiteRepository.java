package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.EventoTramite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoTramiteRepository extends JpaRepository<EventoTramite, Long> {
    List<EventoTramite> findByTramiteIdOrderByOrdenAsc(Long tramiteId);
}
