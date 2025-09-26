package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para generar resoluciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerarResolucionRequest {

    private String decision;
    private String observaciones;
    private String autoridad;
}
