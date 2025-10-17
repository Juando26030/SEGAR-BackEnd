package com.segar.backend.gestionUsuarios.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Vinculación con Keycloak ==========
    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    // ========== Información Personal ==========
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "id_type")
    private String idType; // DNI, CC, CE, Pasaporte, etc.

    @Column(name = "id_number", unique = true)
    private String idNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender; // Masculino, Femenino, Otro

    // ========== Información de Contacto ==========
    @Column(name = "phone")
    private String phone;

    @Column(name = "alt_phone")
    private String altPhone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    // ========== Información Laboral ==========
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "role")
    private String role; // Administrador, Empleado, etc.

    // ========== Auditoría y Control ==========
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    // ========== Métodos Auxiliares ==========
    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        // Auto-generar fullName si no existe
        if (fullName == null && firstName != null && lastName != null) {
            fullName = firstName + " " + lastName;
        }
    }

    @PreUpdate
    public void preUpdate() {
        // Actualizar fullName si cambian firstName o lastName
        if (firstName != null && lastName != null) {
            fullName = firstName + " " + lastName;
        }
    }
}
