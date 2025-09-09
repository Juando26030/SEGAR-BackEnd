package com.segar.backend.tramites.api.dto;

public record NotificationSettingsDTO(
        boolean email,
        boolean sms,
        boolean requirements,
        boolean statusUpdates
) {}
