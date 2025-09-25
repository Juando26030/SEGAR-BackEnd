package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para información de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoInfoDTO {

    private Long id;
    private String nombreArchivo;
    private String tipoDocumento;
    private String rutaArchivo;
    private Long tamanioArchivo;
    private String tipoMime;
    private String fechaCarga;
    private Boolean obligatorio;
}
