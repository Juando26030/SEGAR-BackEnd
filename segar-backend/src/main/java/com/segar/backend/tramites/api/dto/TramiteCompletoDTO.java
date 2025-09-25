package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para trámite completo con toda la información
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramiteCompletoDTO {

    private Long id;
    private String numeroRadicado;
    private String estado;
    private LocalDateTime fechaCreacion;
    private Long empresaId;
    private Long productoId;
    private ResolucionDTO resolucion;
    private RegistroSanitarioDTO registroSanitario;
    private List<HistorialTramiteDTO> historial;
}
