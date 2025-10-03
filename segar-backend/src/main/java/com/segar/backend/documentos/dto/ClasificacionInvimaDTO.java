package com.segar.backend.documentos.dto;

import com.segar.backend.documentos.domain.TipoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO de respuesta para clasificación de productos INVIMA
 * Contiene el resultado de la determinación del trámite y documentos requeridos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionInvimaDTO {

    /**
     * Trámite determinado según la clasificación (NSO, PSA, RSA)
     */
    private TipoTramite tramiteRequerido;

    /**
     * Descripción del trámite
     */
    private String descripcionTramite;

    /**
     * Tiempo estimado de respuesta en días
     */
    private Integer tiempoEstimadoDias;

    /**
     * Lista de documentos obligatorios para este trámite
     */
    private List<DocumentoRequeridoDTO> documentosObligatorios;

    /**
     * Lista de formularios que deben completarse
     */
    private List<String> formulariosRequeridos;

    /**
     * Validaciones específicas que aplican
     */
    private List<String> validacionesAdicionales;

    /**
     * Advertencias importantes
     */
    private List<String> advertencias;

    /**
     * Tarifa estimada (si está disponible)
     */
    private String tarifaEstimada;

    /**
     * Justificación de la clasificación
     */
    private String justificacionClasificacion;

    /**
     * Indica si es coherente la clasificación seleccionada
     */
    private Boolean esClasificacionValida;

    /**
     * Mensaje de validación en caso de error
     */
    private String mensajeValidacion;

    // Información de clasificación utilizada
    private String categoriaAlimento;
    private String nivelRiesgo;
    private String poblacionObjetivo;
    private String tipoProcesamiento;
}