package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.ClasificacionProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClasificacionProductoRepository extends JpaRepository<ClasificacionProducto, Long> {

    Optional<ClasificacionProducto> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);
}
