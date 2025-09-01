package com.segar.backend.models;

/**
 * Categorías de riesgo de productos según clasificación INVIMA
 * Para registros sanitarios de alimentos
 */
public enum CategoriaRiesgo {
    /**
     * Riesgo Alto - Productos que requieren mayor control sanitario
     * Ej: Productos cárnicos, lácteos sin tratamiento térmico
     */
    ALTO("Alto"),

    /**
     * Riesgo Medio - Productos con riesgo moderado
     * Ej: Productos procesados, conservas
     */
    MEDIO("Medio"),

    /**
     * Riesgo Bajo - Productos con menor riesgo sanitario
     * Ej: Productos secos, envasados estériles
     */
    BAJO("Bajo");

    private final String descripcion;

    CategoriaRiesgo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
