package com.segar.backend.documentos.infrastructure;


import com.segar.backend.documentos.domain.DocumentTemplate;
import com.segar.backend.shared.domain.CategoriaRiesgo;
import com.segar.backend.shared.domain.TipoTramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de plantillas de documentos
 */
@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    /**
     * Busca plantilla por código único
     */
    Optional<DocumentTemplate> findByCode(String code);

    /**
     * Busca plantillas activas
     */
    List<DocumentTemplate> findByActiveTrue();

    /**
     * Busca plantillas por tipo de trámite
     */
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.active = true AND :tipoTramite MEMBER OF dt.appliesToTramiteTypes")
    List<DocumentTemplate> findByTipoTramite(@Param("tipoTramite") TipoTramite tipoTramite);

    /**
     * Busca plantillas por tipo de trámite y categoría de riesgo
     */
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.active = true " +
           "AND :tipoTramite MEMBER OF dt.appliesToTramiteTypes " +
           "AND (dt.categoriaRiesgo = :categoriaRiesgo OR dt.categoriaRiesgo IS NULL)")
    List<DocumentTemplate> findByTipoTramiteAndCategoriaRiesgo(
        @Param("tipoTramite") TipoTramite tipoTramite,
        @Param("categoriaRiesgo") CategoriaRiesgo categoriaRiesgo);

    /**
     * Busca plantillas obligatorias para un tipo de trámite
     */
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.active = true " +
           "AND dt.required = true " +
           "AND :tipoTramite MEMBER OF dt.appliesToTramiteTypes")
    List<DocumentTemplate> findRequiredByTipoTramite(@Param("tipoTramite") TipoTramite tipoTramite);

    /**
     * Busca plantillas ordenadas por displayOrder
     */
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.active = true " +
           "AND :tipoTramite MEMBER OF dt.appliesToTramiteTypes " +
           "ORDER BY dt.displayOrder ASC, dt.name ASC")
    List<DocumentTemplate> findByTipoTramiteOrderedByDisplayOrder(@Param("tipoTramite") TipoTramite tipoTramite);

    /**
     * Verifica si existe una plantilla con el mismo código (para validar duplicados)
     */
    boolean existsByCodeAndIdNot(String code, Long id);
}
