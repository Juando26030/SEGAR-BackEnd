package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.EventoTramite;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface EventoTramiteRepository extends JpaRepository<EventoTramite, Long> {
    List<EventoTramite> findByTramiteIdOrderByOrdenAsc(Long tramiteId);
}
