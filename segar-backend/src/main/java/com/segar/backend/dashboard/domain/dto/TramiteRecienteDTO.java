package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramiteRecienteDTO {
    private Long id;
    private String radicadoNumber;
    private String productName;
    private String procedureType;
    private String currentStatus;
    private LocalDateTime lastUpdate;
}
