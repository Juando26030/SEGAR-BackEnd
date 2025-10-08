package com.segar.backend.tramites.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir la clasificación del producto desde el frontend
 * Basado en especificaciones INVIMA para determinación de tipo de trámite
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionProductoDTO {

    @NotBlank(message = "La categoría del producto es obligatoria")
    private String categoria;

    @NotNull(message = "El nivel de riesgo es obligatorio")
    private NivelRiesgo nivelRiesgo;

    @NotBlank(message = "La población objetivo es obligatoria")
    private String poblacionObjetivo;

    @NotBlank(message = "El tipo de procesamiento es obligatorio")
    private String procesamiento;

    private TipoAccion tipoAccion = TipoAccion.REGISTRO;

    private Boolean esImportado = false;

    public enum NivelRiesgo {
        BAJO, MEDIO, ALTO
    }

    public enum TipoAccion {
        REGISTRO, RENOVACION, MODIFICACION
    }
}

