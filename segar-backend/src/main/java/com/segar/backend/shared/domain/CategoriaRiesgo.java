package com.segar.backend.shared.domain;

/**
 * Categorías de riesgo de productos según INVIMA
 */
public enum CategoriaRiesgo {
    I("Riesgo Bajo"),
    IIA("Riesgo Moderado A"),
    IIB("Riesgo Moderado B"),
    III("Riesgo Alto");

    private final String descripcion;

    CategoriaRiesgo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
