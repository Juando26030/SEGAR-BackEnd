package com.segar.backend.tramites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "clasificaciones_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiesgo nivelRiesgo;

    @Column(nullable = false)
    private String poblacionObjetivo;

    @Column(nullable = false)
    private String procesamiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAccion tipoAccion;

    @Column(nullable = false)
    private Boolean esImportado;

    @Column(nullable = false)
    private LocalDateTime fechaClasificacion;

    public enum NivelRiesgo {
        BAJO, MEDIO, ALTO
    }

    public enum TipoAccion {
        REGISTRO, RENOVACION, MODIFICACION
    }
}
