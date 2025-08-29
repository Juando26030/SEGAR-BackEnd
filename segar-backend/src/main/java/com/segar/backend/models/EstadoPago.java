package com.segar.backend.models;

/**
 * Estados posibles de un pago en el sistema SEGAR
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public enum EstadoPago {
    /**
     * Pago pendiente de procesamiento
     */
    PENDIENTE("Pendiente"),

    /**
     * Pago procesado exitosamente
     */
    APROBADO("Aprobado"),

    /**
     * Pago rechazado por la entidad financiera
     */
    RECHAZADO("Rechazado"),

    /**
     * Pago cancelado por el usuario
     */
    CANCELADO("Cancelado"),

    /**
     * Pago en proceso de verificación
     */
    EN_VERIFICACION("En Verificación");

    private final String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
