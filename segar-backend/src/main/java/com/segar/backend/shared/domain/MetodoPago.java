package com.segar.backend.shared.domain;

/**
 * Métodos de pago disponibles
 */
public enum MetodoPago {
    TARJETA_CREDITO("Tarjeta de Crédito"),
    TARJETA_DEBITO("Tarjeta de Débito"),
    PSE("PSE"),
    TRANSFERENCIA("Transferencia Bancaria"),
    EFECTIVO("Efectivo");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
