package com.segar.backend.documentos.domain;

/**
 * Tipos de trámite ante INVIMA según el producto
 */
public enum TipoTramite {
    NSO("Notificación Sanitaria Obligatoria", "NSO"),
    PSA("Permiso Sanitario de Alimentos", "PSA"),
    RSA("Registro Sanitario de Alimentos", "RSA");

    private final String descripcion;
    private final String codigo;

    TipoTramite(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Determina si el trámite requiere renovación periódica
     */
    public boolean requiereRenovacion() {
        return this == PSA || this == RSA;
    }

    /**
     * Obtiene el tiempo de vigencia en años
     */
    public int getVigenciaAnios() {
        return switch (this) {
            case NSO -> 0; // No tiene vigencia específica
            case PSA -> 5;
            case RSA -> 10;
        };
    }

    /**
     * Obtiene el tiempo estimado en días para el trámite
     */
    public int getTiempoEstimadoDias() {
        return switch (this) {
            case NSO -> 15;  // Notificación más rápida
            case PSA -> 45;  // Permiso intermedio
            case RSA -> 90;  // Registro más complejo
        };
    }
}