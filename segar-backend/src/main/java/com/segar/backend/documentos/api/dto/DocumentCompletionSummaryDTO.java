package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resumen de completitud de documentos en un trámite
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCompletionSummaryDTO {

    private Long tramiteId;
    private Integer totalRequired;
    private Integer completedRequired;
    private Integer totalOptional;
    private Integer completedOptional;
    private Double completionPercentage;
    private Boolean allRequiredCompleted;
    private List<DocumentTemplateDTO> missingRequired;
    private List<DocumentTemplateDTO> availableOptional;
}
