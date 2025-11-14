package com.segar.backend.shared.domain;

import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.gestionUsuarios.domain.Usuario;
import com.segar.backend.tramites.domain.EventoTramite;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String radicadoNumber;
    private LocalDate submissionDate;
    private String procedureType;

    @OneToOne
    private Producto product;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    private EstadoTramite currentStatus;
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoTramite> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();


    public void addEvento(EventoTramite evento) {
        eventos.add(evento);
    }

    public void addDocumento(Documento documento) {
        documentos.add(documento);
    }
}
