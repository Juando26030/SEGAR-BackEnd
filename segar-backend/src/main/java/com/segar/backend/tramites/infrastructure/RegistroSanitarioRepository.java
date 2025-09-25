package com.segar.backend.tramites.infrastructure;


import com.segar.backend.tramites.domain.RegistroSanitario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para registros sanitarios
 */
@Repository
public interface RegistroSanitarioRepository extends JpaRepository<RegistroSanitario, Long> {

    Optional<RegistroSanitario> findByResolucionId(Long resolucionId);

    List<RegistroSanitario> findByEmpresaId(Long empresaId);

    List<RegistroSanitario> findByProductoId(Long productoId);

    boolean existsByNumeroRegistro(String numeroRegistro);

    @Query("SELECT r FROM RegistroSanitario r WHERE r.fechaVencimiento < :fecha AND r.estado = 'VIGENTE'")
    List<RegistroSanitario> findRegistrosProximosAVencer(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(r) FROM RegistroSanitario r WHERE YEAR(r.fechaExpedicion) = :year")
    Long countByYear(@Param("year") int year);
}
