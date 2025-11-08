package com.segar.backend.calendario.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "eventos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaEvento categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadEvento prioridad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoEvento estado = EstadoEvento.ACTIVO;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "tramite_id")
    private Long tramiteId;

    @Column(name = "documento_id")
    private Long documentoId;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esVencido() {
        return LocalDate.now().isAfter(this.fecha) &&
                this.estado == EstadoEvento.ACTIVO;
    }

    public boolean esCritico() {
        return this.prioridad == PrioridadEvento.ALTA &&
                this.estado == EstadoEvento.ACTIVO;
    }
}
