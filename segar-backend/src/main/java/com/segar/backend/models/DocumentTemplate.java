package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Plantilla de documento que define la estructura, campos y validaciones
 * para documentos requeridos en trámites INVIMA
 */
@Entity
@Table(name = "document_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único del tipo de documento (ej: FICHA_TECNICA, ETIQUETA)
     * No hardcodeado - configurable desde BD
     */
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    /**
     * Nombre descriptivo del documento
     */
    @Column(nullable = false)
    private String name;

    /**
     * Descripción detallada del documento y su propósito
     */
    @Column(length = 1000)
    private String description;

    /**
     * Definición de campos en formato JSON Schema
     * Define estructura del formulario dinámico
     */
    @Lob
    @Column(name = "fields_definition", columnDefinition = "CLOB")
    private String fieldsDefinition;

    /**
     * Reglas para archivos adjuntos en formato JSON
     * (allowedMimeTypes, maxSizeBytes, multipleAllowed)
     */
    @Lob
    @Column(name = "file_rules", columnDefinition = "CLOB")
    private String fileRules;

    /**
     * Tipos de trámite a los que aplica esta plantilla
     */
    @ElementCollection(targetClass = TipoTramite.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "template_tramite_types",
        joinColumns = @JoinColumn(name = "template_id")
    )
    @Column(name = "tramite_type")
    private Set<TipoTramite> appliesToTramiteTypes;

    /**
     * Versión de la plantilla para control de cambios
     */
    @Builder.Default
    private Integer version = 1;

    /**
     * Indica si la plantilla está activa
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Indica si es obligatoria para el trámite
     */
    @Builder.Default
    private Boolean required = false;

    /**
     * Orden de presentación en el formulario
     */
    private Integer displayOrder;

    /**
     * Categoría de riesgo del producto a la que aplica
     */
    @Enumerated(EnumType.STRING)
    private CategoriaRiesgo categoriaRiesgo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
