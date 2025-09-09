package com.segar.backend.documentos.infrastructure;

import com.segar.backend.models.DocumentInstance;
import com.segar.backend.models.DocumentTemplate;
import com.segar.backend.models.DocumentInstance.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de instancias de documentos
 */
@Repository
public interface DocumentInstanceRepository extends JpaRepository<DocumentInstance, Long> {

    /**
     * Busca instancias por ID de trámite
     */
    List<DocumentInstance> findByTramiteId(Long tramiteId);

    /**
     * Busca instancias por ID de empresa
     */
    List<DocumentInstance> findByEmpresaId(Long empresaId);

    /**
     * Busca instancias por trámite y plantilla
     */
    Optional<DocumentInstance> findByTramiteIdAndTemplate(Long tramiteId, DocumentTemplate template);

    /**
     * Busca instancias por trámite y estado
     */
    List<DocumentInstance> findByTramiteIdAndStatus(Long tramiteId, DocumentStatus status);

    /**
     * Busca instancias finalizadas por trámite
     */
    @Query("SELECT di FROM DocumentInstance di " +
           "WHERE di.tramite.id = :tramiteId " +
           "AND di.status = 'FINALIZED'")
    List<DocumentInstance> findByTramiteIdAndStatusFinalized(@Param("tramiteId") Long tramiteId);

    /**
     * Cuenta documentos obligatorios finalizados para un trámite
     */
    @Query("SELECT COUNT(di) FROM DocumentInstance di " +
           "WHERE di.tramite.id = :tramiteId " +
           "AND di.template.required = true " +
           "AND di.status = 'FINALIZED'")
    long countRequiredFinalizedByTramiteId(@Param("tramiteId") Long tramiteId);

    /**
     * Cuenta documentos obligatorios totales para un trámite
     * Corregido para usar procedureType en lugar de tipoTramite
     */
    @Query("SELECT COUNT(DISTINCT dt) FROM DocumentTemplate dt " +
           "JOIN dt.appliesToTramiteTypes tt " +
           "JOIN Tramite t ON t.id = :tramiteId " +
           "WHERE " +
           "(UPPER(t.procedureType) IN ('REGISTRO', 'REGISTRO_SANITARIO') AND tt = com.segar.backend.models.TipoTramite.REGISTRO) OR " +
           "(UPPER(t.procedureType) IN ('RENOVACION', 'RENOVACION_SANITARIA') AND tt = com.segar.backend.models.TipoTramite.RENOVACION) OR " +
           "(UPPER(t.procedureType) IN ('MODIFICACION', 'MODIFICACION_SANITARIA') AND tt = com.segar.backend.models.TipoTramite.MODIFICACION) " +
           "AND dt.required = true " +
           "AND dt.active = true")
    long countRequiredTemplatesByTramiteId(@Param("tramiteId") Long tramiteId);

    /**
     * Busca instancias con archivos asociados
     */
    List<DocumentInstance> findByTramiteIdAndFileUrlIsNotNull(Long tramiteId);

    /**
     * Busca instancias por empresa y plantilla
     */
    List<DocumentInstance> findByEmpresaIdAndTemplate(Long empresaId, DocumentTemplate template);

    /**
     * Busca la última versión de una instancia por trámite y plantilla
     */
    @Query("SELECT di FROM DocumentInstance di " +
           "WHERE di.tramite.id = :tramiteId " +
           "AND di.template = :template " +
           "ORDER BY di.version DESC, di.updatedAt DESC")
    List<DocumentInstance> findLatestByTramiteAndTemplate(@Param("tramiteId") Long tramiteId,
                                                         @Param("template") DocumentTemplate template);

    /**
     * Verifica si existe una instancia finalizada para una plantilla y trámite
     */
    boolean existsByTramiteIdAndTemplateAndStatus(Long tramiteId, DocumentTemplate template, DocumentStatus status);
}
