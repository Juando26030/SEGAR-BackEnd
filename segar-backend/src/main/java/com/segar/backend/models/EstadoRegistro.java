package com.segar.backend.models;

/**
 * Estados posibles de un registro sanitario
 */
public enum EstadoRegistro {
    VIGENTE("Vigente"),
    VENCIDO("Vencido"),
    SUSPENDIDO("Suspendido");

    private final String descripcion;

    EstadoRegistro(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
