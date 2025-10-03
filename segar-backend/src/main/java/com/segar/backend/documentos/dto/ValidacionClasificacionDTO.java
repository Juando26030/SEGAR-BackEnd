package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para validación de clasificación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionClasificacionDTO {
    
    private Boolean esValida;
    private String mensaje;
    private String tramiteSugerido;
    private String justificacion;
    
}