package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la radicación de solicitud
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRadicadaResponseDTO {

    /**
     * Número de radicado generado automáticamente
     * Formato: INV-20250831-000123
     */
    private String numeroRadicado;

    /**
     * Estado de la solicitud después de radicación
     */
    private String estado;

    /**
     * Fecha y hora de radicación
     */
    private LocalDateTime fechaRadicacion;

    /**
     * ID de la empresa que radicó
     */
    private Long empresaId;

    /**
     * ID del producto para el cual se radicó
     */
    private Long productoId;

    /**
     * Tipo de trámite radicado
     */
    private String tipoTramite;

    /**
     * Mensaje de confirmación
     */
    private String mensaje;
}
