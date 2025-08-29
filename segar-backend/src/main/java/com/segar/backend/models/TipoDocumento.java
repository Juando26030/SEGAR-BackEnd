package com.segar.backend.models;

/**
 * Tipos de documento requeridos para trámites ante INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public enum TipoDocumento {
    /**
     * Certificado de constitución de la empresa
     */
    CERTIFICADO_CONSTITUCION("Certificado de Constitución"),

    /**
     * RUT de la empresa
     */
    RUT("RUT"),

    /**
     * Concepto sanitario de la planta de producción
     */
    CONCEPTO_SANITARIO("Concepto Sanitario"),

    /**
     * Ficha técnica del producto
     */
    FICHA_TECNICA("Ficha Técnica"),

    /**
     * Etiqueta del producto
     */
    ETIQUETA("Etiqueta"),

    /**
     * Análisis microbiológico
     */
    ANALISIS_MICROBIOLOGICO("Análisis Microbiológico"),

    /**
     * Análisis fisicoquímico
     */
    ANALISIS_FISICOQUIMICO("Análisis Fisicoquímico"),

    /**
     * Certificado de buenas prácticas de manufactura
     */
    CERTIFICADO_BPM("Certificado BPM"),

    /**
     * Plan HACCP
     */
    PLAN_HACCP("Plan HACCP");

    private final String descripcion;

    TipoDocumento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
