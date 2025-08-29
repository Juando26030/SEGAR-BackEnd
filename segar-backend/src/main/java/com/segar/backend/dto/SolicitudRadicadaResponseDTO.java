package com.segar.backend.dto;

import com.segar.backend.models.EstadoSolicitud;
import com.segar.backend.models.TipoTramite;
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
     * ID de la solicitud radicada
     */
    private Long id;

    /**
     * Número de radicado generado automáticamente
     */
    private String numeroRadicado;

    /**
     * ID de la empresa
     */
    private Long empresaId;

    /**
     * Nombre del producto
     */
    private String nombreProducto;

    /**
     * Tipo de trámite radicado
     */
    private TipoTramite tipoTramite;

    /**
     * Estado actual de la solicitud
     */
    private EstadoSolicitud estado;

    /**
     * Fecha de radicación
     */
    private LocalDateTime fechaRadicacion;

    /**
     * Observaciones de la solicitud
     */
    private String observaciones;

    /**
     * Mensaje de confirmación
     */
    private String mensaje;
}
