package com.segar.backend.tramites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoTramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id")
    private Tramite tramite;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDate date;
    private boolean completed;
    private boolean currentEvent;
    private Integer orden;
}
