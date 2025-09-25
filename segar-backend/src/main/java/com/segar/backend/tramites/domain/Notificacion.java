package com.segar.backend.tramites.domain;

import com.segar.backend.shared.domain.Tramite;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.segar.backend.shared.domain.TipoNotificacion;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id")
    private Tramite tramite;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion type;

    private String title;

    @Column(length = 2000)
    private String message;

    private LocalDateTime date;
    private boolean read;
}
