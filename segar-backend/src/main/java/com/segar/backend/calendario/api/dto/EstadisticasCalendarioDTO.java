package com.segar.backend.calendario.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadisticasCalendarioDTO {
    private long totalEventos;
    private long eventosCriticos;
    private long eventosCompletados;
    private long eventosVencidos;
    private long eventosActivos;
}
