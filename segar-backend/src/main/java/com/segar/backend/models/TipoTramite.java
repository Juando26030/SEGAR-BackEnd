package com.segar.backend.models;

/**
 * Tipos de trámite disponibles en el sistema SEGAR
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public enum TipoTramite {
    /**
     * Registro sanitario inicial de un producto
     */
    REGISTRO("Registro Sanitario"),

    /**
     * Renovación de registro sanitario existente
     */
    RENOVACION("Renovación de Registro"),

    /**
     * Modificación de registro sanitario existente
     */
    MODIFICACION("Modificación de Registro");

    private final String descripcion;

    TipoTramite(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
