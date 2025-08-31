package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para request de generación de resolución
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerarResolucionRequest {
    private String decision; // APROBAR, RECHAZAR
    private String observaciones;
    private String autoridad;
}
