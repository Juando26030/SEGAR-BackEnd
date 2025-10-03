package com.segar.backend.documentos.domain;

/**
 * Tipos de trámite ante INVIMA según el producto
 * Basado en formularios oficiales INVIMA
 */
public enum TipoTramite {
    NSO("Notificación Sanitaria Obligatoria", "NSO"),
    PSA("Permiso Sanitario de Alimentos", "PSA"),
    RSA("Registro Sanitario de Alimentos", "RSA");

    // Códigos de formularios oficiales INVIMA
    public static final String FORMULARIO_NSO = "ASS-NSA-FM097";
    public static final String FORMULARIO_PSA = "ASS-PSA-FM098"; 
    public static final String FORMULARIO_RSA = "ASS-RSA-FM099";

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
     * Obtiene el código del formulario oficial INVIMA
     */
    public String getCodigoFormulario() {
        return switch (this) {
            case NSO -> FORMULARIO_NSO;
            case PSA -> FORMULARIO_PSA;
            case RSA -> FORMULARIO_RSA;
        };
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
            case NSO -> 0; // No tiene vigencia específica - notificación permanente
            case PSA -> 5; // Permiso Sanitario - 5 años
            case RSA -> 10; // Registro Sanitario - 10 años
        };
    }

    /**
     * Obtiene el tiempo de procesamiento en días según documento INVIMA
     */
    public int getTiempoProcesamiento() {
        return switch (this) {
            case NSO -> 1; // Máximo 24 horas
            case PSA -> 30; // Días a semanas
            case RSA -> 90; // Análisis más estrictos y vigilancia especial
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