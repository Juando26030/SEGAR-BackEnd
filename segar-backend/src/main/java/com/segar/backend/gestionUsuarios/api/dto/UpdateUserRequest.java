package com.segar.backend.gestionUsuarios.api.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    // ========== Datos de Autenticación (Keycloak) ==========
    @Email(message = "El email debe ser válido")
    private String email;

    // ========== Información Personal ==========
    private String firstName;
    private String lastName;
    private String idType; // DNI, CC, CE, Pasaporte
    private String idNumber;
    private LocalDate birthDate;
    private String gender; // Masculino, Femenino, Otro

    // ========== Información de Contacto ==========
    private String phone;
    private String altPhone;
    private String address;
    private String city;
    private String postalCode;

    // ========== Información Laboral ==========
    private String employeeId;
    private String role; // Administrador, Empleado, etc.

    // ========== Estado ==========
    private Boolean enabled;
}

