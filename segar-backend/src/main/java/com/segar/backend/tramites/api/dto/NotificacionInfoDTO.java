package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para información de notificaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionInfoDTO {

    private Long id;
    private String tipo;
    private String titulo;
    private String mensaje;
    private String fecha;
    private Boolean leida;
}
