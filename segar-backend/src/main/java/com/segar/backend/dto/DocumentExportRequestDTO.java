package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar exportación a PDF de un documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExportRequestDTO {

    /**
     * Formato de salida (por defecto PDF)
     */
    @Builder.Default
    private String outputFormat = "pdf";

    /**
     * Incluir archivos embebidos en el PDF
     */
    @Builder.Default
    private Boolean includeEmbeddedFiles = false;

    /**
     * Plantilla de diseño personalizada (opcional)
     */
    private String templateStyle;

    /**
     * Configuraciones adicionales para la generación
     */
    private String exportOptions;
}
