package com.segar.backend.shared.domain;

/**
 * Niveles de riesgo sanitario para alimentos procesados según Resolución 719 de 2015
 * Determina el tipo de trámite requerido ante INVIMA
 */
public enum NivelRiesgo {
    BAJO("Bajo riesgo", "NSO", 
         "Productos secos y estables, no requieren refrigeración ni cadena de frío"),
    
    MEDIO("Riesgo medio", "PSA", 
          "Alimentos procesados que requieren control, validación de parámetros físico-químicos y microbiológicos"),
    
    ALTO("Riesgo alto", "RSA", 
         "Alimentos de mayor complejidad o destinados a poblaciones vulnerables, requieren análisis estrictos");

    private final String descripcion;
    private final String tramiteRequerido;
    private final String caracteristicas;

    NivelRiesgo(String descripcion, String tramiteRequerido, String caracteristicas) {
        this.descripcion = descripcion;
        this.tramiteRequerido = tramiteRequerido;
        this.caracteristicas = caracteristicas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTramiteRequerido() {
        return tramiteRequerido;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    /**
     * Determina si requiere certificación BPM obligatoria
     */
    public boolean requiereBPM() {
        return this == MEDIO || this == ALTO;
    }

    /**
     * Determina si requiere certificación HACCP obligatoria
     */
    public boolean requiereHACCP() {
        return this == ALTO;
    }
}