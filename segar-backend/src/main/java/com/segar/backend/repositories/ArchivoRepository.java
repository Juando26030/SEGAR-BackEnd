package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
