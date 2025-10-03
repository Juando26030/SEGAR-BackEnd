package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para documentos generados automáticamente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoGeneradoDTO {
    
    private String nombre;
    private String tipo;
    private String url;
    private String contenido;
    private Long tamanoBytes;
    private String fechaGeneracion;
    
}