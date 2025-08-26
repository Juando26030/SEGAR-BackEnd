package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.Requerimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {
    List<Requerimiento> findByTramiteId(Long tramiteId);
}
