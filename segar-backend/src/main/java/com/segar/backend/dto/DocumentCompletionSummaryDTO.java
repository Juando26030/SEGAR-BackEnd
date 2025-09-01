package com.segar.backend.dto;

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

    /**
     * ID del trámite
     */
    private Long tramiteId;

    /**
     * Total de documentos obligatorios
     */
    private int totalRequired;

    /**
     * Documentos obligatorios completados
     */
    private int completedRequired;

    /**
     * Total de documentos opcionales
     */
    private int totalOptional;

    /**
     * Documentos opcionales completados
     */
    private int completedOptional;

    /**
     * Porcentaje de completitud (0-100)
     */
    private double completionPercentage;

    /**
     * Indica si todos los documentos obligatorios están completos
     */
    private boolean allRequiredCompleted;

    /**
     * Lista de documentos faltantes obligatorios
     */
    private List<DocumentTemplateDTO> missingRequired;

    /**
     * Lista de documentos disponibles opcionales
     */
    private List<DocumentTemplateDTO> availableOptional;
}
