package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    // Métodos para multi-tenancy
    List<Tramite> findByEmpresaId(Long empresaId);

    List<Tramite> findByEmpresaIdOrderByLastUpdateDesc(Long empresaId);

    @Query("SELECT t FROM Tramite t WHERE t.empresaId = :empresaId AND t.currentStatus = :status")
    List<Tramite> findByEmpresaIdAndStatus(@Param("empresaId") Long empresaId,
                                           @Param("status") com.segar.backend.shared.domain.EstadoTramite status);

}
