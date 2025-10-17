package com.segar.backend.notificaciones.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para filtros de búsqueda de correos electrónicos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSearchFilter {

    // Búsqueda general de texto (en asunto, contenido y remitente)
    private String searchText;

    // Filtros específicos
    private String fromAddress;
    private String subject;
    private Boolean isRead;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Paginación
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "receivedDate";

    @Builder.Default
    private String sortDirection = "DESC";

    // Filtro por tipo de correo
    private String type; // INBOUND, OUTBOUND

    // Filtro por estado
    private String status; // SENT, RECEIVED, etc.

    // Filtro por archivos adjuntos
    private Boolean hasAttachments;
}
