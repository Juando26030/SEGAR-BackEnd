package com.segar.backend.dto;

import com.segar.backend.models.DocumentInstance.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de instancias de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentInstanceDTO {

    private Long id;
    private Long templateId;
    private String templateCode;
    private String templateName;
    private Long tramiteId;
    private Long empresaId;
    private DocumentStatus status;
    private String filledData;
    private String fileUrl;
    private String fileMime;
    private Long fileSize;
    private String storageKey;
    private String metadata;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // Campos adicionales para la UI
    private Boolean isRequired;
    private String templateDescription;
    private String fieldsDefinition;
    private String fileRules;
}
