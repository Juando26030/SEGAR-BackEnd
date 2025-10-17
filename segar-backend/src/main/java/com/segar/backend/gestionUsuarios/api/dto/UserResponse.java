package com.segar.backend.gestionUsuarios.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // ========== IDs y vinculación ==========
    private Long id;
    private String keycloakId;
    private String username;
    private String email;

    // ========== Información Personal ==========
    private String firstName;
    private String lastName;
    private String fullName;
    private String idType;
    private String idNumber;
    private LocalDate birthDate;
    private String gender;

    // ========== Información de Contacto ==========
    private String phone;
    private String altPhone;
    private String address;
    private String city;
    private String postalCode;

    // ========== Información Laboral ==========
    private String employeeId;
    private String role;

    // ========== Estado y Auditoría ==========
    private Boolean enabled;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    private List<String> roles;
}
