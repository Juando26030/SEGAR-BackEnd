package com.segar.backend.shared.infrastructure;

import com.segar.backend.shared.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos por nombre, descripción, referencia o fabricante
     * Búsqueda case-insensitive
     */
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.referencia) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.fabricante) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscarProductos(@Param("query") String query);

    /**
     * Busca productos por empresa
     */
    List<Producto> findByEmpresaId(Long empresaId);

    /**
     * Busca productos por nombre y empresa
     */
    @Query("SELECT p FROM Producto p WHERE p.empresaId = :empresaId AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.referencia) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.fabricante) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Producto> buscarProductosPorEmpresa(@Param("query") String query, @Param("empresaId") Long empresaId);
    List<Producto> findByEmpresaId(Long empresaId);

    @Query("SELECT p FROM Producto p WHERE p.empresaId = :empresaId AND p.id NOT IN (SELECT t.product.id FROM Tramite t WHERE t.product IS NOT NULL)")
    List<Producto> findByEmpresaIdAndNotAssociatedWithTramites(@Param("empresaId") Long empresaId);

}
