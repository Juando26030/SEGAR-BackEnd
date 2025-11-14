package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificationRequestDTO {
    
    private String categoria;   
    private String poblacion;
    private String procesamiento;

}
