package com.segar.backend.tramites.repositories;

import com.segar.backend.tramites.model.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {
}
