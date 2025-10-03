package com.segar.backend.documentos.domain;

/**
 * Categorías principales de alimentos procesados según clasificación INVIMA
 * Basado en la Resolución 719 de 2015 y normativas de alimentos procesados
 */
public enum CategoriaAlimento {
    PANADERIA_PASTELERIA("Panadería y pastelería", "Panes, tortas, bizcochos, galletas"),
    GALLETERIA_CONFITERIA("Galletería y confitería", "Galletas, caramelos, chocolates, chicles"),
    DERIVADOS_LACTEOS("Derivados lácteos", "Yogures, quesos, bebidas lácteas fermentadas"),
    PRODUCTOS_CARNICOS("Productos cárnicos procesados", "Embutidos, jamones, salchichas, curados"),
    PESCADOS_MARISCOS("Pescados y mariscos procesados", "Conservas, empacados al vacío, congelados"),
    JUGOS_BEBIDAS("Jugos, néctares y bebidas no alcohólicas", "Bebidas procesadas pasteurizadas"),
    CONSERVAS_FRUTAS_VEGETALES("Conservas de frutas y vegetales", "En almíbar, en salmuera, al natural"),
    SALSAS_CONDIMENTOS("Salsas, aderezos y condimentos", "Salsas procesadas, condimentos preparados"),
    ALIMENTOS_INFANTILES("Alimentos infantiles procesados", "Cereales infantiles, compotas, papillas"),
    PRODUCTOS_LISTOS_CONSUMO("Productos listos para consumo", "Precocidos, congelados, empacados al vacío"),
    OTROS_ALIMENTOS("Otros alimentos procesados", "Categoría genérica para productos no específicos");

    private final String descripcion;
    private final String ejemplos;

    CategoriaAlimento(String descripcion, String ejemplos) {
        this.descripcion = descripcion;
        this.ejemplos = ejemplos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEjemplos() {
        return ejemplos;
    }

    /**
     * Determina el nivel de riesgo por defecto según la categoría de alimento
     * Basado en regulaciones INVIMA y riesgo asociado
     */
    public NivelRiesgo getNivelRiesgoDefault() {
        return switch (this) {
            case ALIMENTOS_INFANTILES, DERIVADOS_LACTEOS, PRODUCTOS_CARNICOS -> NivelRiesgo.ALTO;
            case PESCADOS_MARISCOS, JUGOS_BEBIDAS -> NivelRiesgo.MEDIO;
            case PANADERIA_PASTELERIA, GALLETERIA_CONFITERIA, CONSERVAS_FRUTAS_VEGETALES,
                 SALSAS_CONDIMENTOS, PRODUCTOS_LISTOS_CONSUMO, OTROS_ALIMENTOS -> NivelRiesgo.BAJO;
        };
    }

    /**
     * Valida si la categoría requiere información adicional específica
     */
    public boolean requiereInformacionEspecial() {
        return this == ALIMENTOS_INFANTILES || this == DERIVADOS_LACTEOS || this == PRODUCTOS_CARNICOS;
    }
}