package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.Requerimiento;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {
    List<Requerimiento> findByTramiteId(Long tramiteId);
}
