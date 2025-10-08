package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un campo individual de un documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampoDocumentoDTO {

    private String nombre;

    private String tipo; // text, number, date, textarea, file, select

    private Boolean requerido;

    private String placeholder;

    private String descripcion;

    private String[] opciones; // Para campos tipo select

    private String valorDefecto;
}

