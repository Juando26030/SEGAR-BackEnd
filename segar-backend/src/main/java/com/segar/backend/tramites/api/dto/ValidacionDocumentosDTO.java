package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de validación de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionDocumentosDTO {

    private Boolean completo;

    private Integer progresoGlobal;

    private Integer documentosCompletos;

    private Integer documentosTotales;

    private List<String> documentosPendientes;

    private List<String> errores;

    private Boolean puedeRadicar;
}

