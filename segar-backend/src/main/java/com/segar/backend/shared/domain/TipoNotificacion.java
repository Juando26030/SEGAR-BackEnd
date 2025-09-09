package com.segar.backend.shared.domain;

/**
 * Tipos de notificaciones del sistema
 */
public enum TipoNotificacion {
    GENERAL("General"),
    STATUS_UPDATE("Actualización de Estado"),
    REQUIREMENT("Requerimiento"),
    PAYMENT("Pago"),
    DOCUMENT("Documento");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
