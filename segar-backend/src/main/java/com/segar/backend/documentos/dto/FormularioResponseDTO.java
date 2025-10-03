package com.segar.backend.documentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de procesamiento de formularios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormularioResponseDTO {
    
    private Boolean esExitoso;
    private String mensaje;
    private String numeroRadicado;
    private String tipoTramite;
    private String estadoTramite;
    private Integer tiempoEstimadoDias;
    private List<String> errores;
    private List<DocumentoGeneradoDTO> documentosGenerados;
    private String fechaRadicacion;
    
}