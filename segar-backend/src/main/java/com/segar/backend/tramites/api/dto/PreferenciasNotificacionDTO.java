package com.segar.backend.tramites.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para preferencias de notificación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciasNotificacionDTO {

    private Boolean email;
    private Boolean sms;
    private Boolean requirements;
    private Boolean statusUpdates;
}
