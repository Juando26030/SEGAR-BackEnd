package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que contiene el resultado de la clasificación del producto
 * Incluye el tipo de trámite determinado y los documentos requeridos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoClasificacionDTO {

    private TipoTramiteINVIMA tramite;

    private String tramiteDescripcion;

    private List<DocumentoRequeridoDTO> documentos;

    private List<String> advertencias;

    private String tiempoEstimado;

    private String costoEstimado;

    public enum TipoTramiteINVIMA {
        NSO, PSA, RSA
    }
}

