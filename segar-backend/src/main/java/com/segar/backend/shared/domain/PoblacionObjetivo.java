package com.segar.backend.shared.domain;

/**
 * Poblaciones objetivo para productos alimenticios según clasificación INVIMA
 * Determina requisitos especiales y tipo de trámite
 */
public enum PoblacionObjetivo {
    POBLACION_GENERAL("Población general", 
                      "La más usada para productos de consumo masivo"),
    
    ALIMENTACION_INFANTIL("Alimentación infantil", 
                          "Productos procesados para niños mayores de 1 año"),
    
    ALIMENTACION_BEBES("Alimentación para bebés menores de 1 año", 
                       "Fórmulas, compotas, cereales infantiles para lactantes"),
    
    MUJERES_GESTANTES_LACTANTES("Mujeres gestantes o lactantes", 
                                 "Productos especializados para embarazadas y madres lactantes"),
    
    ADULTOS_MAYORES("Adultos mayores", 
                    "Productos adaptados para población geriátrica"),
    
    DEPORTISTAS("Deportistas", 
                "Suplementos y alimentos para rendimiento deportivo"),
    
    DIETAS_ESPECIALES("Dietas especiales / condiciones médicas", 
                      "Libres de gluten, bajos en sodio, alimentos para control metabólico");

    private final String descripcion;
    private final String caracteristicas;

    PoblacionObjetivo(String descripcion, String caracteristicas) {
        this.descripcion = descripcion;
        this.caracteristicas = caracteristicas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    /**
     * Determina si es una población sensible que requiere RSA
     */
    public boolean esPoblacionSensible() {
        return this == ALIMENTACION_INFANTIL || 
               this == ALIMENTACION_BEBES || 
               this == MUJERES_GESTANTES_LACTANTES || 
               this == ADULTOS_MAYORES;
    }

    /**
     * Determina si requiere estudios nutricionales adicionales
     */
    public boolean requiereEstudiosNutricionales() {
        return esPoblacionSensible() || this == DIETAS_ESPECIALES;
    }

    /**
     * Obtiene las advertencias obligatorias en etiquetado
     */
    public String getAdvertenciasEtiquetado() {
        return switch (this) {
            case ALIMENTACION_BEBES -> "La lactancia materna es el mejor alimento para el niño";
            case ALIMENTACION_INFANTIL -> "Producto para uso infantil bajo supervisión";
            case MUJERES_GESTANTES_LACTANTES -> "Consulte con su médico antes del consumo";
            case ADULTOS_MAYORES -> "Producto especializado para adultos mayores";
            case DIETAS_ESPECIALES -> "Para uso en dietas especiales según indicación médica";
            case DEPORTISTAS -> "Suplemento deportivo, no exceder dosis recomendada";
            default -> null;
        };
    }
}
