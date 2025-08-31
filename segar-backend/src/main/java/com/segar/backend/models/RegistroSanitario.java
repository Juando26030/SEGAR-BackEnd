package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para registros sanitarios emitidos por INVIMA
 */
@Entity
@Table(name = "registros_sanitarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroSanitario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaExpedicion;

    @Column(nullable = false)
    private LocalDateTime fechaVencimiento;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRegistro estado;

    @Column(nullable = false)
    private Long resolucionId;

    private String documentoUrl;
}
