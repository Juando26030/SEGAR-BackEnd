package com.segar.backend.tramites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.segar.backend.documentos.domain.Archivo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaRequerimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requerimiento_id")
    private Requerimiento requerimiento;

    private LocalDateTime fecha;

    @Column(length = 4000)
    private String mensaje;

    @OneToMany(mappedBy = "ownerRespuesta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Archivo> archivos = new ArrayList<>();
}
