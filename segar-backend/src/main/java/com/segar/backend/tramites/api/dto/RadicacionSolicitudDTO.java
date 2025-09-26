package com.segar.backend.tramites.api.dto;

import com.segar.backend.shared.domain.TipoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para radicación de solicitudes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadicacionSolicitudDTO {

    private Long empresaId;
    private Long productoId;
    private TipoTramite tipoTramite;
    private List<Long> documentosId;
    private Long pagoId;
    private String observaciones;
}
