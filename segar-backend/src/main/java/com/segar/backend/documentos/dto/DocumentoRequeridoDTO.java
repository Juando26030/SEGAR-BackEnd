package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para documentos requeridos en trámites INVIMA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoRequeridoDTO {
    
    private String nombre;
    private String descripcion;
    private Boolean esObligatorio;
    private Boolean esGenerableEnApp;
    private String observaciones;
    private String formato;
    private Integer tamanioMaximoMB;
    
}