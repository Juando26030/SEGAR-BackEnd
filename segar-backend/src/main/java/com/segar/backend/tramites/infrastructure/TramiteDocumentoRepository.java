package com.segar.backend.tramites.infrastructure;

import com.segar.backend.tramites.domain.TramiteDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para TramiteDocumento
 */
@Repository
public interface TramiteDocumentoRepository extends JpaRepository<TramiteDocumento, Long> {

    /**
     * Encuentra todos los documentos de una solicitud
     */
    List<TramiteDocumento> findBySolicitudId(Long solicitudId);

    /**
     * Encuentra un documento específico de una solicitud
     */
    Optional<TramiteDocumento> findBySolicitudIdAndDocumentoId(Long solicitudId, String documentoId);

    /**
     * Cuenta documentos completos de una solicitud
     */
    long countBySolicitudIdAndEstado(Long solicitudId, String estado);

    /**
     * Elimina todos los documentos de una solicitud
     */
    void deleteBySolicitudId(Long solicitudId);
}

