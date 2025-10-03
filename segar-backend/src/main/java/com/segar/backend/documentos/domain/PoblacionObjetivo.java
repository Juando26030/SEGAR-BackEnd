package com.segar.backend.documentos.domain;

/**
 * Población objetivo según normativas INVIMA
 */
public enum PoblacionObjetivo {
    POBLACION_GENERAL("Población general", "Para consumo de la población en general"),
    POBLACION_ESPECIAL("Población especial", "Para grupos específicos (infantil, adulto mayor, etc.)"),
    POBLACION_INFANTIL("Población infantil", "Específicamente para menores de 12 años"),
    POBLACION_ADULTO_MAYOR("Adulto mayor", "Para personas mayores de 60 años"),
    POBLACION_DEPORTISTAS("Deportistas", "Para atletas y personas con alta actividad física");

    private final String descripcion;
    private final String detalle;

    PoblacionObjetivo(String descripcion, String detalle) {
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
     * Determina si requiere evaluación especial
     */
    public boolean requiereEvaluacionEspecial() {
        return this != POBLACION_GENERAL;
    }

    /**
     * Determina si es población sensible
     */
    public boolean esPoblacionSensible() {
        return this == POBLACION_INFANTIL || this == POBLACION_ADULTO_MAYOR;
    }

    /**
     * Determina si requiere estudios nutricionales especiales
     */
    public boolean requiereEstudiosNutricionales() {
        return this == POBLACION_INFANTIL || this == POBLACION_DEPORTISTAS;
    }

    /**
     * Obtiene las advertencias de etiquetado necesarias
     */
    public String getAdvertenciasEtiquetado() {
        return switch (this) {
            case POBLACION_INFANTIL -> "No recomendado para menores de [edad] años";
            case POBLACION_ADULTO_MAYOR -> "Consulte con su médico antes del consumo";
            case POBLACION_DEPORTISTAS -> "Suplemento dietario - No sustituye una dieta equilibrada";
            case POBLACION_ESPECIAL -> "Para uso específico según indicaciones";
            default -> "";
        };
    }
}