package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para documentos generados automáticamente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentosGeneradosDTO {
    
    private Boolean esExitoso;
    private String mensaje;
    private List<DocumentoGeneradoDTO> documentos;
    
}