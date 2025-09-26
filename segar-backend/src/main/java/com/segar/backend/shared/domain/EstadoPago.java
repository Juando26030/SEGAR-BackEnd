package com.segar.backend.shared.domain;

/**
 * Estados posibles de un pago
 */
public enum EstadoPago {
    PENDIENTE("Pendiente"),
    PROCESANDO("Procesando"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
