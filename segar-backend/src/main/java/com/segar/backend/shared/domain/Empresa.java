package com.segar.backend.shared.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empresa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String razonSocial;

    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    @Column(length = 100)
    private String nombreComercial;

    @Column(length = 200)
    private String direccion;

    @Column(length = 50)
    private String ciudad;

    @Column(length = 50)
    private String pais;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String representanteLegal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEmpresa estado;

    @Column(length = 50)
    private String tipoEmpresa; // FABRICANTE, IMPORTADOR, DISTRIBUIDOR
}
