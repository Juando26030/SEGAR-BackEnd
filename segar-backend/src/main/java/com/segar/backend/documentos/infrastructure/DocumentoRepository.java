package com.segar.backend.documentos.infrastructure;

import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.shared.domain.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;




@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

}
