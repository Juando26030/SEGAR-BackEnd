package com.segar.backend.models.DTOs;

public record NotificationSettingsDTO(
        boolean email,
        boolean sms,
        boolean requirements,
        boolean statusUpdates
) {}
