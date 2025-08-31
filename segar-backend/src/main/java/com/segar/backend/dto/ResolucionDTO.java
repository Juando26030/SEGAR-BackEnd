package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resoluciones INVIMA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolucionDTO {
    private Long id;
    private String numeroResolucion;
    private LocalDateTime fechaEmision;
    private String autoridad;
    private String estado; // APROBADA, RECHAZADA, EN_REVISION
    private String observaciones;
    private Long tramiteId;
    private String documentoUrl;
    private LocalDateTime fechaNotificacion;
}
