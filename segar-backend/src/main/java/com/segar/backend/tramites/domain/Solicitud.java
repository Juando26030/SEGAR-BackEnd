package com.segar.backend.tramites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.shared.domain.EstadoSolicitud;
import com.segar.backend.documentos.domain.Documento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una solicitud de trámite ante INVIMA
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID de la empresa que presenta la solicitud
     * Relación con entidad Usuario/Empresa (por implementar en futuros pasos)
     */
    private Long empresaId;

    /**
     * Producto asociado a la solicitud
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Tipo de trámite solicitado
     */
    @Enumerated(EnumType.STRING)
    private TipoTramite tipoTramite;

    /**
     * Estado actual de la solicitud
     */
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    /**
     * Número de radicado único generado automáticamente
     * Formato: INV-{timestamp}
     */
    @Column(unique = true)
    private String numeroRadicado;

    /**
     * Fecha y hora de radicación de la solicitud
     */
    private LocalDateTime fechaRadicacion;

    /**
     * Observaciones adicionales de la solicitud
     */
    @Column(length = 1000)
    private String observaciones;

    /**
     * Documentos asociados a la solicitud
     */
    // Usar solo IDs:
    @ElementCollection
    @CollectionTable(name = "solicitud_documentos", joinColumns = @JoinColumn(name = "solicitud_id"))
    @Column(name = "documento_id")
    private List<Long> documentosIds;

    /**
     * Pago asociado a la solicitud
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    private Pago pago;
}
