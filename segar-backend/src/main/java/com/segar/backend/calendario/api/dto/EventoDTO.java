package com.segar.backend.calendario.api.dto;

import com.segar.backend.calendario.domain.CategoriaEvento;
import com.segar.backend.calendario.domain.EstadoEvento;
import com.segar.backend.calendario.domain.PrioridadEvento;
import com.segar.backend.calendario.domain.TipoEvento;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class EventoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora;
    private TipoEvento tipo;
    private CategoriaEvento categoria;
    private PrioridadEvento prioridad;
    private EstadoEvento estado;
    private Long empresaId;
    private Long tramiteId;
    private Long usuarioId;
    private Long documentoId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
