package com.segar.backend.documentos.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String tipoMime;
    private Long tamano;
    private String rutaAlmacenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respuesta_id")
    private com.segar.backend.tramites.domain.RespuestaRequerimiento ownerRespuesta;
}
