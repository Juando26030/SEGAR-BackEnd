package com.segar.backend.models;

/**
 * Estados posibles de una resolución
 */
public enum EstadoResolucion {
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    EN_REVISION("En Revisión");

    private final String descripcion;

    EstadoResolucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
