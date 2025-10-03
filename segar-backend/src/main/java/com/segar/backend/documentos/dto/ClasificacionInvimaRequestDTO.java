package com.segar.backend.documentos.dto;

import com.segar.backend.documentos.domain.CategoriaAlimento;
import com.segar.backend.documentos.domain.NivelRiesgo;
import com.segar.backend.documentos.domain.PoblacionObjetivo;
import com.segar.backend.documentos.domain.TipoProcesamiento;
import com.segar.backend.documentos.domain.TipoTramite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de clasificación de productos en INVIMA
 * Contiene los 4 campos principales de clasificación según documentación INVIMA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionInvimaRequestDTO {

    /**
     * Campo 1: Categoría del Alimento
     * Selección de la categoría principal del producto
     */
    @NotNull(message = "La categoría del alimento es obligatoria")
    private CategoriaAlimento categoriaAlimento;

    /**
     * Campo 2: Nivel de Riesgo  
     * Determinación sanitaria según Resolución 719 de 2015
     */
    @NotNull(message = "El nivel de riesgo es obligatorio")
    private NivelRiesgo nivelRiesgo;

    /**
     * Campo 3: Población Objetivo
     * Grupo destinatario al que se dirige el producto
     */
    @NotNull(message = "La población objetivo es obligatoria")
    private PoblacionObjetivo poblacionObjetivo;

    /**
     * Campo 4: Tipo de Procesamiento
     * Método tecnológico de conservación principal
     */
    @NotNull(message = "El tipo de procesamiento es obligatorio")
    private TipoProcesamiento tipoProcesamiento;

    // Información adicional del producto para contexto
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    private String descripcionProducto;

    // Datos de la empresa (opcional para clasificación inicial)
    private String nombreEmpresa;
    private String nitEmpresa;

    // Campos para productos importados
    private Boolean esImportado;
    private String paisOrigen;
    private String fabricanteExtranjero;

    /**
     * Valida si la combinación de campos es coherente según matriz INVIMA
     */
    public boolean esCombinaciValida() {
        // Validar población sensible requiere RSA
        if (poblacionObjetivo.esPoblacionSensible() && nivelRiesgo != NivelRiesgo.ALTO) {
            return false;
        }

        // Validar coherencia entre categoría y nivel de riesgo
        NivelRiesgo riesgoEsperado = categoriaAlimento.getNivelRiesgoDefault();
        if (riesgoEsperado == NivelRiesgo.BAJO && nivelRiesgo == NivelRiesgo.ALTO) {
            // Solo permitir si es población sensible o procesamiento complejo
            return poblacionObjetivo.esPoblacionSensible() || 
                   tipoProcesamiento.requiereEstudiosEstabilidad();
        }

        return true;
    }

    /**
     * Determina el trámite resultante según la clasificación
     */
    public TipoTramite getTramiteResultante() {
        // Población sensible siempre requiere RSA
        if (poblacionObjetivo.esPoblacionSensible()) {
            return TipoTramite.RSA;
        }

        // Según nivel de riesgo
        return switch (nivelRiesgo) {
            case BAJO -> TipoTramite.NSO;
            case MEDIO -> TipoTramite.PSA;
            case ALTO -> TipoTramite.RSA;
        };
    }
}