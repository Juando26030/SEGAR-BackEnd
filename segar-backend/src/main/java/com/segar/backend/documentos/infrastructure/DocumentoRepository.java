package com.segar.backend.documentos.infrastructure;

import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.shared.domain.TipoDocumento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;




@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    @Query(value = "SELECT * FROM documento WHERE tramite_id = :tramiteId", nativeQuery = true)
    List<Documento> findByTramiteId(@Param("tramiteId") Long tramiteId);
}
