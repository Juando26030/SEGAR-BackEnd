package com.segar.backend.documentos.domain;

/**
 * Tipos de procesamiento según INVIMA
 */
public enum TipoProcesamiento {
    TERMICO("Procesamiento térmico", "Tratamiento con calor (pasteurización, esterilización)"),
    NO_TERMICO("Procesamiento no térmico", "Sin aplicación de calor"),
    FERMENTACION("Fermentación", "Proceso de fermentación controlada"),
    DESHIDRATACION("Deshidratación", "Eliminación de humedad"),
    CONGELACION("Congelación", "Conservación por frío"),
    EMPACADO_VACIO("Empacado al vacío", "Sin presencia de oxígeno"),
    OTROS("Otros procesamientos", "Métodos no clasificados anteriormente");

    private final String descripcion;
    private final String detalle;

    TipoProcesamiento(String descripcion, String detalle) {
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
     * Determina si el procesamiento incrementa el nivel de riesgo
     */
    public boolean incrementaRiesgo() {
        return this == NO_TERMICO || this == EMPACADO_VACIO;
    }

    /**
     * Determina si requiere validación térmica
     */
    public boolean requiereValidacionTermica() {
        return this == TERMICO || this == FERMENTACION;
    }

    /**
     * Determina si requiere estudios de estabilidad
     */
    public boolean requiereEstudiosEstabilidad() {
        return this == NO_TERMICO || this == EMPACADO_VACIO || this == DESHIDRATACION;
    }
}