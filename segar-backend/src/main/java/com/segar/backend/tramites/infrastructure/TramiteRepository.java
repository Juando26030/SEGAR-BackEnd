package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TramiteRepository extends JpaRepository<Tramite, Long> {
}
