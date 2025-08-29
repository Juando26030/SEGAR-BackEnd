package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago de tarifas para trámites ante INVIMA
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Monto del pago
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /**
     * Método de pago utilizado
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    /**
     * Estado del pago
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    /**
     * Referencia de la transacción o comprobante
     */
    @Column(unique = true)
    private String referenciaPago;

    /**
     * Fecha y hora del pago
     */
    private LocalDateTime fechaPago;

    /**
     * Descripción del concepto de pago
     */
    private String concepto;

    /**
     * Solicitud asociada al pago
     */
    @OneToOne(mappedBy = "pago")
    private Solicitud solicitud;
}
