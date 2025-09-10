package com.segar.backend.documentos.infrastructure;

import com.segar.backend.documentos.domain.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
