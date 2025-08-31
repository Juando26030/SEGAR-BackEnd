package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para resoluciones emitidas por INVIMA
 */
@Entity
@Table(name = "resoluciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroResolucion;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private String autoridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoResolucion estado;

    @Column(length = 2000)
    private String observaciones;

    @Column(nullable = false)
    private Long tramiteId;

    private String documentoUrl;

    private LocalDateTime fechaNotificacion;
}
