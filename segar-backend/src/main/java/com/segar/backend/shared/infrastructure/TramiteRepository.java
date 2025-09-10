package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TramiteRepository extends JpaRepository<Tramite, Long> {
}
