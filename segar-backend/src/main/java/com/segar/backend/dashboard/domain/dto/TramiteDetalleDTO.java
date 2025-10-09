package com.segar.backend.dashboard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramiteDetalleDTO {

    // Información básica del trámite
    private Long id;
    private String radicadoNumber;
    private LocalDate submissionDate;
    private String procedureType;
    private String productName;
    private String currentStatus;
    private LocalDateTime lastUpdate;

    // Eventos del trámite
    private List<EventoTramiteDTO> eventos;

    // Requerimientos (si los tiene)
    private List<RequerimientoInfoDTO> requerimientos;

    // Notificaciones recientes
    private List<NotificacionInfoDTO> notificaciones;

    // Historial del trámite
    private List<HistorialTramiteDTO> historial;

    // Estadísticas útiles
    private EstadisticasTramiteDTO estadisticas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventoTramiteDTO {
        private String title;
        private String description;
        private LocalDate date;
        private boolean completed;
        private boolean currentEvent;
        private int orden;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequerimientoInfoDTO {
        private String number;
        private String title;
        private String description;
        private LocalDate deadline;
        private String status;
        private long diasRestantes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificacionInfoDTO {
        private String type;
        private String title;
        private String message;
        private LocalDateTime date;
        private boolean read;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistorialTramiteDTO {
        private LocalDateTime fecha;
        private String accion;
        private String descripcion;
        private String usuario;
        private String estado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasTramiteDTO {
        private long diasTranscurridos;
        private int totalEventos;
        private int eventosCompletados;
        private int requerimientosPendientes;
        private int notificacionesNoLeidas;
        private double porcentajeProgreso;
    }
}
