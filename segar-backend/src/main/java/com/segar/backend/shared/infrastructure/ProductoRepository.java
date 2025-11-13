package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE p.empresaId = :empresaId AND p.id NOT IN (SELECT t.product.id FROM Tramite t WHERE t.product IS NOT NULL)")
    List<Producto> findByEmpresaIdAndNotAssociatedWithTramites(@Param("empresaId") Long empresaId);

    @Query("SELECT DISTINCT p FROM Producto p " +
            "JOIN RegistroSanitario rs ON rs.productoId = p.id " +
            "WHERE rs.estado = 'VIGENTE' " +
            "AND rs.fechaVencimiento > CURRENT_TIMESTAMP")
    List<Producto> findProductosConRegistrosSanitariosVigentes();

    @Query("SELECT DISTINCT p FROM Producto p " +
            "JOIN RegistroSanitario rs ON rs.productoId = p.id " +
            "WHERE p.empresaId = :empresaId " +
            "AND rs.estado = 'VIGENTE' " +
            "AND rs.fechaVencimiento > CURRENT_TIMESTAMP")
    List<Producto> findProductosConRegistrosSanitariosVigentesByEmpresaId(@Param("empresaId") Long empresaId);

    List<Producto> findByEmpresaId(Long empresaId);

}
