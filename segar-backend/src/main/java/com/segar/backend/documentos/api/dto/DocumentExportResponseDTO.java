package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de exportación de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExportResponseDTO {

    private String fileUrl;
    private Long fileSize;
    private String fileMime;
    private String storageKey;
    private String suggestedFileName;
    private String status;
    private String message;
}
