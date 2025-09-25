package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para registros sanitarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroSanitarioDTO {

    private Long id;
    private String numeroRegistro;
    private LocalDateTime fechaExpedicion;
    private LocalDateTime fechaVencimiento;
    private Long productoId;
    private Long empresaId;
    private String estado;
    private Long resolucionId;
    private String documentoUrl;
}
