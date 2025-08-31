package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para historial de trámites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialTramiteDTO {
    private Long id;
    private Long tramiteId;
    private LocalDateTime fecha;
    private String accion;
    private String descripcion;
    private String usuario;
    private String estado;
}
