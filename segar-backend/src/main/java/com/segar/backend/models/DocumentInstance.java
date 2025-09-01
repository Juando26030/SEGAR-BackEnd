package com.segar.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Instancia concreta de un documento rellenado por el usuario
 * basado en una DocumentTemplate
 */
@Entity
@Table(name = "document_instance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Plantilla base para este documento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private DocumentTemplate template;

    /**
     * Trámite al que pertenece esta instancia
     * Puede ser null para documentos generales
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id")
    private Tramite tramite;

    /**
     * Empresa propietaria del documento
     */
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    /**
     * Estado actual del documento
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.DRAFT;

    /**
     * Datos rellenados del formulario en formato JSON
     * Corresponde a los campos definidos en template.fieldsDefinition
     */
    @Lob
    @Column(name = "filled_data", columnDefinition = "CLOB")
    private String filledData;

    /**
     * URL del archivo final generado (PDF o archivo adjunto)
     */
    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    /**
     * Tipo MIME del archivo
     */
    @Column(name = "file_mime")
    private String fileMime;

    /**
     * Tamaño del archivo en bytes
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Clave de almacenamiento interno del archivo
     */
    @Column(name = "storage_key", length = 1000)
    private String storageKey;

    /**
     * Metadatos adicionales en formato JSON
     */
    @Lob
    @Column(name = "metadata", columnDefinition = "CLOB")
    private String metadata;

    /**
     * Versión del documento (para historial de cambios)
     */
    @Builder.Default
    private Integer version = 1;

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

    /**
     * Estados posibles de un documento
     */
    public enum DocumentStatus {
        DRAFT,      // Borrador - en construcción
        FILLED,     // Rellenado - datos completos
        UPLOADED,   // Archivo cargado
        VERIFIED,   // Verificado por el sistema
        FINALIZED   // Finalizado - listo para envío
    }
}
