package com.segar.backend.tramites.model;

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
    private String productName;
    @Enumerated(EnumType.STRING)
    private EstadoTramite currentStatus;
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoTramite> eventos = new ArrayList<>();
}
