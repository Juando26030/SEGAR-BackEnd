package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
