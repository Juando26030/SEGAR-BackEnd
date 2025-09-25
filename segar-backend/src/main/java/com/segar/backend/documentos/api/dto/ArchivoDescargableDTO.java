package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para archivos descargables
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoDescargableDTO {

    private Long id;
    private String nombre;
    private String tipoMime;
    private Long tamano;
    private String urlDescarga;
    private String rutaAlmacenamiento;
}
