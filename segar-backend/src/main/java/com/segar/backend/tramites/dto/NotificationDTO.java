package com.segar.backend.tramites.dto;

public record NotificationDTO(
        Long id,
        String type,
        String title,
        String message,
        String date,
        boolean read
) {}
