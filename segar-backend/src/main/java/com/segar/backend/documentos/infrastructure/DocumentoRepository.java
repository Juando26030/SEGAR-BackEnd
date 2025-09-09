package com.segar.backend.documentos.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.segar.backend.models.Documento;
import com.segar.backend.models.TipoDocumento;

import java.util.List;

/**
 * Repositorio para la entidad Documento
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    /**
     * Busca documentos por ID de solicitud
     * @param solicitudId ID de la solicitud
     * @return Lista de documentos asociados a la solicitud
     */
    List<Documento> findBySolicitudId(Long solicitudId);

    /**
     * Busca documentos por tipo
     * @param tipoDocumento Tipo de documento
     * @return Lista de documentos del tipo especificado
     */
    List<Documento> findByTipoDocumento(TipoDocumento tipoDocumento);

    /**
     * Busca documentos obligatorios por solicitud
     * @param solicitudId ID de la solicitud
     * @return Lista de documentos obligatorios
     */
    @Query("SELECT d FROM Documento d WHERE d.solicitud.id = :solicitudId AND d.obligatorio = true")
    List<Documento> findDocumentosObligatoriosBySolicitudId(@Param("solicitudId") Long solicitudId);

    /**
     * Cuenta documentos obligatorios por solicitud
     * @param solicitudId ID de la solicitud
     * @return Número de documentos obligatorios
     */
    @Query("SELECT COUNT(d) FROM Documento d WHERE d.solicitud.id = :solicitudId AND d.obligatorio = true")
    long countDocumentosObligatoriosBySolicitudId(@Param("solicitudId") Long solicitudId);

    /**
     * Verifica si existen todos los documentos obligatorios para un tipo de trámite
     * @param solicitudId ID de la solicitud
     * @param tiposObligatorios Lista de tipos de documento obligatorios
     * @return true si están todos los documentos obligatorios
     */
    @Query("SELECT COUNT(DISTINCT d.tipoDocumento) = :cantidadObligatorios " +
           "FROM Documento d WHERE d.solicitud.id = :solicitudId " +
           "AND d.tipoDocumento IN :tiposObligatorios")
    boolean existenTodosLosDocumentosObligatorios(
        @Param("solicitudId") Long solicitudId,
        @Param("tiposObligatorios") List<TipoDocumento> tiposObligatorios,
        @Param("cantidadObligatorios") long cantidadObligatorios
    );

    /**
     * Busca documentos por empresa (usado para validaciones de radicación)
     * @param empresaId ID de la empresa
     * @return Lista de documentos de la empresa
     */
    @Query("SELECT d FROM Documento d WHERE d.solicitud.empresaId = :empresaId")
    List<Documento> findByEmpresaId(@Param("empresaId") Long empresaId);
}
