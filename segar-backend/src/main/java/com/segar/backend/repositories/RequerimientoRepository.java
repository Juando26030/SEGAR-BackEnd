package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.Requerimiento;

import java.util.List;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {
    List<Requerimiento> findByTramiteId(Long tramiteId);
}
