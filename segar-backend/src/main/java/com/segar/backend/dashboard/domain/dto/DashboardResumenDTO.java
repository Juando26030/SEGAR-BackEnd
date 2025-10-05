package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResumenDTO {
    private long totalTramites;
    private List<ConteoPorEstadoDTO> tramitesPorEstado;

    private long totalRegistros;
    private long registrosVigentes;
    private long registrosPorVencer;
    private long registrosVencidos;

    private long requerimientosPendientes;
}
