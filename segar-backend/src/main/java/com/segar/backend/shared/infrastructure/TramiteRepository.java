package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    @Query(value = """
    SELECT t.* 
    FROM tramite t
    JOIN producto p ON t.product_id = p.id
    WHERE p.nombre = :nombre
    """, nativeQuery = true)
    Optional<Tramite> findByNombreProducto(@Param("nombre") String nombre);
}
