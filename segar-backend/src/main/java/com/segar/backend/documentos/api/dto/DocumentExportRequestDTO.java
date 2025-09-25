package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de exportación de documentos a PDF
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExportRequestDTO {

    private String format;
    private Boolean includeMetadata;
    private String watermark;
    private Boolean compress;
}
