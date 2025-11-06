package com.segar.backend.calendario.api.dto;

import com.segar.backend.calendario.domain.CategoriaEvento;
import com.segar.backend.calendario.domain.PrioridadEvento;
import com.segar.backend.calendario.domain.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CrearEventoDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalTime hora;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipo;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaEvento categoria;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadEvento prioridad;

    private Long empresaId;
    private Long tramiteId;
    private Long usuarioId;
    private Long documentoId;
}
