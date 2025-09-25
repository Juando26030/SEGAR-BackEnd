package com.segar.backend.tramites.api.dto;

import com.segar.backend.shared.domain.TipoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de solicitud radicada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRadicadaResponseDTO {

    private String numeroRadicado;
    private String estado;
    private LocalDateTime fechaRadicacion;
    private Long empresaId;
    private Long productoId;
    private String tipoTramite;
    private String mensaje;
}
