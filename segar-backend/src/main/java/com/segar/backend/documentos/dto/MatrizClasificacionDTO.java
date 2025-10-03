package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para matriz de clasificación INVIMA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatrizClasificacionDTO {
    
    private List<String> categorias;
    private List<String> nivelesRiesgo;
    private List<String> poblaciones;
    private List<String> procesamientos;
    private List<EscenarioClasificacionDTO> escenarios;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EscenarioClasificacionDTO {
        private String categoria;
        private String nivelRiesgo;
        private String poblacion;
        private String procesamiento;
        private String tramiteRequerido;
        private Boolean esValido;
    }
    
}