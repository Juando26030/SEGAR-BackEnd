package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.RespuestaRequerimiento;

public interface RespuestaRequerimientoRepository extends JpaRepository<RespuestaRequerimiento, Long> {
}
