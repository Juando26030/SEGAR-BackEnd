package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa un documento requerido para el trámite
 * Basado en especificaciones INVIMA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRequeridoDTO {

    private String id;

    private String nombre;

    private TipoDocumentoRequerido tipo;

    private FormatoDocumento formato;

    private String descripcion;

    private CategoriaDocumento categoria;

    private Boolean obligatorio;

    private Integer orden;

    private String icono;

    private List<CampoDocumentoDTO> campos;

    public enum TipoDocumentoRequerido {
        AUTOGENERADO, EXTERNO
    }

    public enum FormatoDocumento {
        PDF, JSON, IMAGE, MULTI
    }

    public enum CategoriaDocumento {
        BASICO, ANALISIS, CERTIFICACION, ESTUDIOS, OTROS
    }
}

