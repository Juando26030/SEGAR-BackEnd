package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaGlobalDTO {
    private List<ResultadoTramiteDTO> tramites;
    private List<ResultadoRegistroDTO> registros;
    private int totalTramites;
    private int totalRegistros;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoTramiteDTO {
        private Long id;
        private String radicadoNumber;
        private String productName;
        private String procedureType;
        private String currentStatus;
        private LocalDate submissionDate;
        private LocalDateTime lastUpdate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoRegistroDTO {
        private Long id;
        private String numeroRegistro;
        private String productName;
        private String estado;
        private LocalDateTime fechaExpedicion;
        private LocalDateTime fechaVencimiento;
    }
}
