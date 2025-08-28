package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.EventoTramite;

import java.util.List;

public interface EventoTramiteRepository extends JpaRepository<EventoTramite, Long> {
    List<EventoTramite> findByTramiteIdOrderByOrdenAsc(Long tramiteId);
}
