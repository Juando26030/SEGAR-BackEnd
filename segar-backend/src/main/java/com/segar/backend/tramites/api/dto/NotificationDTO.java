package com.segar.backend.tramites.api.dto;

public record NotificationDTO(
        Long id,
        String type,
        String title,
        String message,
        String date,
        boolean read
) {}
