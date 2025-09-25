package com.segar.backend.shared.domain;

/**
 * Estados posibles de un registro sanitario
 */
public enum EstadoRegistro {
    VIGENTE("Vigente"),
    VENCIDO("Vencido"),
    SUSPENDIDO("Suspendido"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoRegistro(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
