package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface TramiteRepository extends JpaRepository<Tramite, Long> {

}
