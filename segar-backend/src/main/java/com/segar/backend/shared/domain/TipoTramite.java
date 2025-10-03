package com.segar.backend.shared.domain;

/**
 * Tipos de trámites disponibles en INVIMA según nivel de riesgo
 * Basado en la Resolución 719 de 2015
 */
public enum TipoTramite {
    // Trámites principales por nivel de riesgo
    NSO("Notificación Sanitaria Obligatoria", "Bajo riesgo", "Productos secos y estables"),
    PSA("Permiso Sanitario", "Riesgo medio", "Productos que requieren control microbiológico"),
    RSA("Registro Sanitario", "Riesgo alto", "Productos complejos o poblaciones vulnerables"),
    
    // Trámites complementarios (mantener compatibilidad)
    REGISTRO("Registro Sanitario", "General", "Trámite genérico de registro"),
    RENOVACION("Renovación Sanitaria", "General", "Renovación de registros existentes"),
    MODIFICACION("Modificación Sanitaria", "General", "Modificación de registros existentes");

    private final String descripcion;
    private final String nivelRiesgo;
    private final String caracteristicas;

    TipoTramite(String descripcion, String nivelRiesgo, String caracteristicas) {
        this.descripcion = descripcion;
        this.nivelRiesgo = nivelRiesgo;
        this.caracteristicas = caracteristicas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    /**
     * Determina si es un trámite de clasificación INVIMA (NSO/PSA/RSA)
     */
    public boolean esClasificacionInvima() {
        return this == NSO || this == PSA || this == RSA;
    }

    /**
     * Obtiene el tiempo estimado de respuesta en días
     */
    public int getTiempoEstimadoDias() {
        return switch (this) {
            case NSO -> 1; // Máximo 24 horas
            case PSA -> 15; // Días a semanas
            case RSA -> 45; // Semanas a meses
            case REGISTRO, RENOVACION, MODIFICACION -> 30; // Genérico
        };
    }

    /**
     * Determina si requiere pago de tarifa
     */
    public boolean requierePago() {
        return true; // Todos los trámites INVIMA requieren pago
    }
}
