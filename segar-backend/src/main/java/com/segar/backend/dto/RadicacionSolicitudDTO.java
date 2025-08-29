package com.segar.backend.dto;

import com.segar.backend.models.TipoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para el request de radicación de solicitud
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadicacionSolicitudDTO {

    /**
     * ID de la empresa que presenta la solicitud
     */
    private Long empresaId;

    /**
     * ID del producto para el cual se solicita el trámite
     */
    private Long productoId;

    /**
     * Tipo de trámite a radicar
     */
    private TipoTramite tipoTramite;

    /**
     * Lista de IDs de documentos asociados a la solicitud
     */
    private List<Long> documentosId;

    /**
     * ID del pago asociado a la solicitud
     */
    private Long pagoId;

    /**
     * Observaciones adicionales para la solicitud
     */
    private String observaciones;
}
