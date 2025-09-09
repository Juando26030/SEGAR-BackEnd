package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.RespuestaRequerimiento;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RespuestaRequerimientoRepository extends JpaRepository<RespuestaRequerimiento, Long> {
}
