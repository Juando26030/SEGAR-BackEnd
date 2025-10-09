package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoPendienteDTO {
    private Long id;
    private Long tramiteId;
    private String number;
    private String title;
    private LocalDate deadline;
    private long diasRestantes;
}
