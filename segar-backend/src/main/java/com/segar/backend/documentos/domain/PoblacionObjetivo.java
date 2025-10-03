package com.segar.backend.documentos.domain;

/**
 * Población objetivo según normativas INVIMA
 */
public enum PoblacionObjetivo {
    POBLACION_GENERAL("Población general", "Para consumo de la población en general"),
    POBLACION_ESPECIAL("Población especial", "Para grupos específicos (infantil, adulto mayor, etc.)"),
    POBLACION_INFANTIL("Población infantil", "Específicamente para menores de 12 años"),
    BEBES_MENORES_1_ANO("Bebés menores de 1 año", "Fórmulas, compotas, cereales infantiles para bebés"),
    MUJERES_GESTANTES("Mujeres gestantes", "Productos específicos para el embarazo"),
    MUJERES_LACTANTES("Mujeres lactantes", "Productos para madres en período de lactancia"),
    POBLACION_ADULTO_MAYOR("Adulto mayor", "Para personas mayores de 60 años"),
    POBLACION_DEPORTISTAS("Deportistas", "Para atletas y personas con alta actividad física"),
    DIETAS_ESPECIALES("Dietas especiales/condiciones médicas", "Libres de gluten, bajos en sodio, control metabólico");

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
        return this == POBLACION_INFANTIL || this == BEBES_MENORES_1_ANO || 
               this == MUJERES_GESTANTES || this == MUJERES_LACTANTES || 
               this == POBLACION_ADULTO_MAYOR || this == DIETAS_ESPECIALES;
    }

    /**
     * Determina si requiere estudios nutricionales especiales
     */
    public boolean requiereEstudiosNutricionales() {
        return this == POBLACION_INFANTIL || this == BEBES_MENORES_1_ANO ||
               this == MUJERES_GESTANTES || this == MUJERES_LACTANTES ||
               this == POBLACION_DEPORTISTAS || this == DIETAS_ESPECIALES;
    }

    /**
     * Obtiene las advertencias de etiquetado necesarias
     */
    public String getAdvertenciasEtiquetado() {
        return switch (this) {
            case BEBES_MENORES_1_ANO -> "La lactancia materna es el mejor alimento para el niño";
            case POBLACION_INFANTIL -> "No recomendado para menores de [edad] años";
            case MUJERES_GESTANTES -> "Consulte con su médico durante el embarazo";
            case MUJERES_LACTANTES -> "Consulte con su médico durante la lactancia";
            case POBLACION_ADULTO_MAYOR -> "Consulte con su médico antes del consumo";
            case POBLACION_DEPORTISTAS -> "Suplemento dietario - No sustituye una dieta equilibrada";
            case DIETAS_ESPECIALES -> "Para uso específico según indicaciones médicas";
            case POBLACION_ESPECIAL -> "Para uso específico según indicaciones";
            default -> "";
        };
    }
}