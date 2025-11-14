package com.segar.backend.documentos.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.segar.backend.shared.domain.Tramite;

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

    private String bucketName;
    private String objectName;
    private String nombreEmpresa;
    private String nombreProducto;
    private String idDocumento;
    private String nombreArchivo;
    private String contentType;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tramite_id")
    private Tramite tramite;

    public Documento(String bucketName, String objectName, String nombreEmpresa, String nombreProducto, String idDocumento, String nombreArchivo, String contentType) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.contentType = contentType;
        this.nombreEmpresa = nombreEmpresa;
        this.nombreProducto = nombreProducto;
        this.idDocumento = idDocumento;
        this.nombreArchivo = nombreArchivo;
        this.uploadedAt = LocalDateTime.now();
    }
}
