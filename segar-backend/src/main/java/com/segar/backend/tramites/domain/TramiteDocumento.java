package com.segar.backend.tramites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un documento de trámite INVIMA
 * Usado para el sistema dinámico de clasificación de documentos
 */
@Entity
@Table(name = "tramite_documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramiteDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID de la solicitud asociada
     */
    @Column(nullable = false)
    private Long solicitudId;

    /**
     * ID del tipo de documento (ej: certificado_existencia, ficha_tecnica_basica)
     */
    @Column(nullable = false)
    private String documentoId;

    /**
     * Datos del documento en formato JSON
     */
    @Column(columnDefinition = "TEXT")
    private String datos;

    /**
     * URL donde se almacena el archivo (si aplica)
     */
    private String archivoUrl;

    /**
     * Nombre del archivo original
     */
    private String nombreArchivo;

    /**
     * Tamaño del archivo en bytes
     */
    private Long tamanioArchivo;

    /**
     * Estado del documento: PENDIENTE, EN_PROGRESO, COMPLETO
     */
    @Column(nullable = false)
    private String estado = "PENDIENTE";

    /**
     * Progreso de completitud (0-100)
     */
    @Column(nullable = false)
    private Integer progreso = 0;

    /**
     * Fecha de creación del documento
     */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de última actualización
     */
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}

