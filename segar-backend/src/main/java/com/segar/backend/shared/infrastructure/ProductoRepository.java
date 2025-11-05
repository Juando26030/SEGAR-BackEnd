package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEmpresaId(Long empresaId);

    @Query("SELECT p FROM Producto p WHERE p.empresaId = :empresaId AND p.id NOT IN (SELECT t.product.id FROM Tramite t WHERE t.product IS NOT NULL)")
    List<Producto> findByEmpresaIdAndNotAssociatedWithTramites(@Param("empresaId") Long empresaId);

}
