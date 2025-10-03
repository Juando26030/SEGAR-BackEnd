package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para tipo de formulario disponible
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoFormularioDTO {
    
    private String codigo;
    private String nombre;
    private String descripcion;
    private String nivelRiesgo;
    private List<String> categorias;
    private Integer tiempoEstimadoDias;
    private String tarifaEstimada;
    
}