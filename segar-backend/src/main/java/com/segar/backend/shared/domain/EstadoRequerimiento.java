package com.segar.backend.shared.domain;

/**
 * Estados posibles de un requerimiento
 */
public enum EstadoRequerimiento {
    PENDIENTE("Pendiente"),
    RESPONDIDO("Respondido"),
    VENCIDO("Vencido");

    private final String descripcion;

    EstadoRequerimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
