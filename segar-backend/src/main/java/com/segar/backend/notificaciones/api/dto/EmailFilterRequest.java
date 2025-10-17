package com.segar.backend.notificaciones.api.dto;

import com.segar.backend.notificaciones.domain.EmailStatus;
import com.segar.backend.notificaciones.domain.EmailType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para filtros de búsqueda de correos electrónicos
 */
@Data
public class EmailFilterRequest {

    private String fromAddress;
    private String subject;
    private EmailType type;
    private EmailStatus status;
    private Boolean isRead;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean hasAttachments;
    private String searchTerm; // Búsqueda general en asunto y contenido

    // Parámetros de paginación
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
