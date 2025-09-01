package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un documento asociado a una solicitud
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del archivo del documento
     */
    @Column(nullable = false)
    private String nombreArchivo;

    /**
     * Tipo de documento según los requerimientos de INVIMA
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipoDocumento;

    /**
     * Ruta o URL donde se almacena el documento
     */
    @Column(nullable = false)
    private String rutaArchivo;

    /**
     * Tamaño del archivo en bytes
     */
    private Long tamanioArchivo;

    /**
     * Tipo MIME del archivo
     */
    private String tipoMime;

    /**
     * Fecha de carga del documento
     */
    private LocalDateTime fechaCarga;

    /**
     * Solicitud a la que pertenece este documento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;

    /**
     * Indica si el documento es obligatorio para el tipo de trámite
     */
    private boolean obligatorio;

    /**
     * Contenido o referencia del archivo
     * Para testing puede contener contenido simulado
     */
    @Column(length = 1000)
    private String archivo;

    // ========== CAMPOS PARA INTEGRACIÓN CON DOCUMENTOS DINÁMICOS ==========
    // Campos opcionales para mantener compatibilidad y permitir migración gradual

    /**
     * Referencia a la plantilla de documento dinámico (opcional)
     * Permite vincular documentos existentes con el nuevo sistema
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private DocumentTemplate template;

    /**
     * Referencia a la instancia de documento dinámico (opcional)
     * Para documentos generados con el nuevo sistema
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private DocumentInstance instance;

    /**
     * Metadatos adicionales en formato JSON (opcional)
     * Para almacenar información extra sin romper estructura existente
     */
    @Lob
    @Column(name = "metadata", columnDefinition = "CLOB")
    private String metadata;

    /**
     * Clave de almacenamiento para el nuevo sistema de archivos (opcional)
     */
    @Column(name = "storage_key", length = 500)
    private String storageKey;
}
