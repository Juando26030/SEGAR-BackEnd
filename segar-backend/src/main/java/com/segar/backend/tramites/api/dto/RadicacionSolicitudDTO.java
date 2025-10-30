package com.segar.backend.tramites.api.dto;

import com.segar.backend.shared.domain.EstadoTramite;
import com.segar.backend.shared.domain.TipoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para radicación de solicitudes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadicacionSolicitudDTO {

    private Long productoId;
    private String procedureType;
    private String radicadoNumber;
    private Long usuarioId;

}
