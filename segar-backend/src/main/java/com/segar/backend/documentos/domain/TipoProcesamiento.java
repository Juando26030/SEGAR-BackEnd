package com.segar.backend.documentos.domain;

/**
 * Tipos de procesamiento según INVIMA
 */
public enum TipoProcesamiento {
    // Métodos básicos de conservación
    REFRIGERADO("Refrigerado", "Conservación por frío controlado 2-8°C"),
    CONGELADO("Congelado", "Conservación por congelación -18°C o menor"),
    
    // Tratamientos térmicos
    PASTEURIZADO("Pasteurizado", "Tratamiento térmico de pasteurización"),
    ESTERILIZADO("Esterilizado", "Esterilización comercial en autoclave"),
    UHT_ESTERILIZACION("UHT/Esterilización", "Ultra alta temperatura para lácteos"),
    HORNEADO("Horneado", "Cocción en horno para panificación"),
    
    // Métodos físicos
    EMPACADO_VACIO("Empacado al vacío", "Sin presencia de oxígeno"),
    CONSERVA_LATA("Conserva en lata o frasco", "Envasado hermético esterilizado"),
    DESHIDRATADO("Deshidratado/Secado", "Eliminación de humedad"),
    ATMOSFERA_MODIFICADA("Empaque en atmósfera modificada (MAP)", "Gases protectores en empaque"),
    
    // Métodos químicos y biológicos
    ADITIVOS_CONSERVANTES("Uso de aditivos o conservantes autorizados", "Conservación química autorizada"),
    FERMENTACION_CONTROLADA("Fermentación controlada", "Proceso de fermentación controlada para lácteos"),
    
    // Métodos industriales específicos
    CONGELACION_IQF("Congelación IQF", "Congelación individual rápida"),
    EXTRACCION_SIMPLE("Extracción simple", "Para aceites y extractos naturales"),
    REFINACION_INDUSTRIAL("Refinación industrial", "Procesamiento industrial para aceites"),
    MOLIENDA_SIMPLE("Molienda simple", "Para cereales y harinas básicas"),
    EMBUTIDOS_CURADOS("Embutidos curados", "Proceso de curado para cárnicos"),
    COCCION_SIMPLE("Cocción simple", "Cocción básica para productos cárnicos"),
    
    // Métodos especiales
    SINTESIS_QUIMICA("Síntesis química", "Para edulcorantes artificiales"),
    EXTRACCION_NATURAL("Extracción natural", "Para edulcorantes naturales"),
    ENCAPSULACION("Encapsulación", "Para suplementos dietéticos"),
    TABLETEADO("Tableteado", "Formación de tabletas para suplementos"),
    SIMPLE_MEZCLA("Simple mezcla", "Mezcla básica sin procesamiento complejo"),
    FORTIFICACION_VITAMINAS("Fortificación con vitaminas", "Adición de vitaminas y minerales"),
    
    // Métodos combinados
    COMBINADO("Combinado", "Combinación de métodos (ej: pasteurizado + refrigerado)"),
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
     * Determina si requiere validación térmica
     */
    public boolean requiereValidacionTermica() {
        return this == PASTEURIZADO || this == ESTERILIZADO || this == UHT_ESTERILIZACION || 
               this == FERMENTACION_CONTROLADA || this == CONSERVA_LATA || this == HORNEADO;
    }

    /**
     * Determina si requiere estudios de estabilidad
     */
    public boolean requiereEstudiosEstabilidad() {
        return this == EMPACADO_VACIO || this == DESHIDRATADO || this == ATMOSFERA_MODIFICADA || 
               this == CONSERVA_LATA || this == ADITIVOS_CONSERVANTES || this == CONGELACION_IQF ||
               this == EMBUTIDOS_CURADOS || this == ENCAPSULACION || this == TABLETEADO;
    }

    /**
     * Determina si incrementa el nivel de riesgo
     */
    public boolean incrementaRiesgo() {
        return this == EMPACADO_VACIO || this == ATMOSFERA_MODIFICADA || this == ADITIVOS_CONSERVANTES ||
               this == EMBUTIDOS_CURADOS || this == SINTESIS_QUIMICA || this == ENCAPSULACION;
    }

    /**
     * Determina si es apto para población infantil
     */
    public boolean esAptoParaInfantil() {
        return this == HORNEADO || this == PASTEURIZADO || this == UHT_ESTERILIZACION || 
               this == ESTERILIZADO || this == FORTIFICACION_VITAMINAS || this == SIMPLE_MEZCLA;
    }
}