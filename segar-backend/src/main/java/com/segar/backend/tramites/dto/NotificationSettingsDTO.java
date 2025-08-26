package com.segar.backend.tramites.dto;

public record NotificationSettingsDTO(
        boolean email,
        boolean sms,
        boolean requirements,
        boolean statusUpdates
) {}
