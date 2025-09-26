package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByTramiteIdOrderByDateDesc(Long tramiteId);
}
