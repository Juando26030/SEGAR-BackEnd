package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de información de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String url;
    private Long tamano;
    private String estado;
    private String mensaje;
}
