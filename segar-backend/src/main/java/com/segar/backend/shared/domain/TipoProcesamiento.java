package com.segar.backend.shared.domain;

/**
 * Tipos de procesamiento y métodos de conservación para alimentos procesados
 * Según clasificación INVIMA y tecnologías de conservación autorizadas
 */
public enum TipoProcesamiento {
    REFRIGERADO("Refrigerado", "Conservación en cadena de frío entre 2-6°C"),
    
    CONGELADO("Congelado", "Conservación por congelación a -18°C o menos"),
    
    PASTEURIZADO("Pasteurizado", "Tratamiento térmico para eliminar patógenos"),
    
    ESTERILIZADO("Esterilizado", "Tratamiento térmico para eliminar todos los microorganismos"),
    
    ENVASADO_VACIO("Envasado al vacío", "Eliminación del aire del envase para conservación"),
    
    CONSERVA_LATA_FRASCO("Conserva en lata o frasco", "Envasado hermético con tratamiento térmico"),
    
    DESHIDRATADO_SECADO("Deshidratado / Secado", "Eliminación de humedad para conservación"),
    
    ATMOSFERA_MODIFICADA("Empaque en atmósfera modificada (MAP)", "Sustitución del aire por gases inertes"),
    
    ADITIVOS_CONSERVANTES("Uso de aditivos o conservantes autorizados", "Conservantes químicos permitidos"),
    
    COMBINADO("Combinado", "Combinación de métodos (ej: pasteurizado + refrigerado)");

    private final String descripcion;
    private final String caracteristicas;

    TipoProcesamiento(String descripcion, String caracteristicas) {
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
     * Determina si requiere validación de proceso térmico
     */
    public boolean requiereValidacionTermica() {
        return this == PASTEURIZADO || 
               this == ESTERILIZADO || 
               this == CONSERVA_LATA_FRASCO;
    }

    /**
     * Determina si requiere cadena de frío
     */
    public boolean requiereCadenaFrio() {
        return this == REFRIGERADO || this == CONGELADO;
    }

    /**
     * Determina si requiere estudios de estabilidad específicos
     */
    public boolean requiereEstudiosEstabilidad() {
        return this == ESTERILIZADO || 
               this == CONSERVA_LATA_FRASCO || 
               this == ATMOSFERA_MODIFICADA ||
               this == COMBINADO;
    }

    /**
     * Obtiene el nivel de riesgo típico asociado a este procesamiento
     */
    public NivelRiesgo getNivelRiesgoTipico() {
        return switch (this) {
            case DESHIDRATADO_SECADO, ADITIVOS_CONSERVANTES -> NivelRiesgo.BAJO;
            case REFRIGERADO, PASTEURIZADO, ENVASADO_VACIO, CONSERVA_LATA_FRASCO -> NivelRiesgo.MEDIO;
            case CONGELADO, ESTERILIZADO, ATMOSFERA_MODIFICADA, COMBINADO -> NivelRiesgo.ALTO;
        };
    }
}
