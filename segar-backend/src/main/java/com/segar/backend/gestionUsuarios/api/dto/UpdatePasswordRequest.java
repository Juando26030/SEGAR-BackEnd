package com.segar.backend.gestionUsuarios.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String newPassword;

    private Boolean temporary = false;
}

