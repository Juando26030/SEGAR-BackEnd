package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.segar.backend.models.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {
}
