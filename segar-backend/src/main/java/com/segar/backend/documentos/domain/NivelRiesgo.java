package com.segar.backend.documentos.domain;

/**
 * Niveles de riesgo para productos alimenticios según INVIMA
 */
public enum NivelRiesgo {
    ALTO("Alto riesgo", "Productos que requieren mayor supervisión"),
    MEDIO("Riesgo moderado", "Productos con riesgo controlado"),
    BAJO("Bajo riesgo", "Productos con mínimo riesgo sanitario");

    private final String descripcion;
    private final String detalle;

    NivelRiesgo(String descripcion, String detalle) {
        this.descripcion = descripcion;
        this.detalle = detalle;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    /**
     * Determina si requiere Buenas Prácticas de Manufactura
     */
    public boolean requiereBPM() {
        return this == ALTO || this == MEDIO;
    }

    /**
     * Determina si requiere sistema HACCP
     */
    public boolean requiereHACCP() {
        return this == ALTO;
    }
}