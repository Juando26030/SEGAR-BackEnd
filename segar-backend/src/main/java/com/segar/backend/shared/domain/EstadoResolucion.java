package com.segar.backend.shared.domain;

/**
 * Estados posibles de una resolución INVIMA
 */
public enum EstadoResolucion {
    BORRADOR("Borrador"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    NOTIFICADA("Notificada");

    private final String descripcion;

    EstadoResolucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

