package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerieMesDTO {
    private int mes; // 1-12
    private long cantidad;
}
