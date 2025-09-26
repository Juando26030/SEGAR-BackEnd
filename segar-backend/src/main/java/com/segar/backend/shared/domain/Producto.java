package com.segar.backend.shared.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
    private String especificaciones;
    private String referencia;
    private String fabricante;

    public Producto(String nombre, String descripcion, String especificaciones, String referencia, String fabricante) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.especificaciones = especificaciones;
        this.referencia = referencia;
        this.fabricante = fabricante;
    }
}
