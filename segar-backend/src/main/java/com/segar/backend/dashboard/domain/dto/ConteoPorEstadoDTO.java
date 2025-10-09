package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteoPorEstadoDTO {
    private String estado;
    private long cantidad;
}
