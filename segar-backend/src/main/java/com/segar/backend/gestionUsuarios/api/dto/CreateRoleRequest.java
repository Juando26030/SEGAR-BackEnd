package com.segar.backend.gestionUsuarios.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    private String description;
}
