package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
