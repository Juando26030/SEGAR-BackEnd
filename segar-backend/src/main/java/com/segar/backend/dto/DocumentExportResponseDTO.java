package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de exportación a PDF
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExportResponseDTO {

    /**
     * URL del archivo PDF generado
     */
    private String fileUrl;

    /**
     * Tamaño del archivo en bytes
     */
    private Long fileSize;

    /**
     * Tipo MIME del archivo generado
     */
    private String fileMime;

    /**
     * Clave de almacenamiento interno
     */
    private String storageKey;

    /**
     * Nombre sugerido para descarga
     */
    private String suggestedFileName;

    /**
     * Estado del proceso de exportación
     */
    private String status;

    /**
     * Mensaje informativo (en caso de errores o advertencias)
     */
    private String message;
}
