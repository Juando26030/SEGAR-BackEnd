package com.segar.backend.documentos.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
