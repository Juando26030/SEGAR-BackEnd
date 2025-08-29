package com.segar.backend.models;

/**
 * Métodos de pago disponibles para las tarifas de trámites INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public enum MetodoPago {
    /**
     * Pago mediante tarjeta de crédito
     */
    TARJETA_CREDITO("Tarjeta de Crédito"),

    /**
     * Pago mediante tarjeta débito
     */
    TARJETA_DEBITO("Tarjeta Débito"),

    /**
     * Pago mediante transferencia bancaria
     */
    TRANSFERENCIA_BANCARIA("Transferencia Bancaria"),

    /**
     * Pago en efectivo en entidad bancaria
     */
    EFECTIVO("Efectivo"),

    /**
     * Pago mediante PSE
     */
    PSE("PSE");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
