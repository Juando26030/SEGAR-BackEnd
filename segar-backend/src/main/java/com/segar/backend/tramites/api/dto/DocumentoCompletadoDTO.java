package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para guardar un documento completado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoCompletadoDTO {

    private String documentoId;

    private Map<String, Object> datos;

    private String archivoUrl;

    private EstadoDocumento estado;

    private Integer progreso;

    private LocalDateTime fechaCarga;

    public enum EstadoDocumento {
        PENDIENTE, EN_PROGRESO, COMPLETO
    }
}

