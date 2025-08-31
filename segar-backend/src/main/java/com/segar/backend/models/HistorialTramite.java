package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para el historial de cambios de un trámite
 */
@Entity
@Table(name = "historial_tramites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tramiteId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String accion;

    @Column(length = 1000)
    private String descripcion;

    private String usuario;

    @Column(nullable = false)
    private String estado;
}
