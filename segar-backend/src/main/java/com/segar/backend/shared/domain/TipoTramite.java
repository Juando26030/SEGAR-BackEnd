package com.segar.backend.shared.domain;

/**
 * Tipos de trámites disponibles en INVIMA
 */
public enum TipoTramite {
    REGISTRO("Registro Sanitario"),
    RENOVACION("Renovación Sanitaria"),
    MODIFICACION("Modificación Sanitaria");

    private final String descripcion;

    TipoTramite(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
