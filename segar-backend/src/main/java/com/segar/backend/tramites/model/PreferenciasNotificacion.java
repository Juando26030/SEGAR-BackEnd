package com.segar.backend.tramites.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciasNotificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tramite_id", unique = true)
    private Tramite tramite;

    private boolean email;
    private boolean sms;
    private boolean requirements;
    private boolean statusUpdates;
}
