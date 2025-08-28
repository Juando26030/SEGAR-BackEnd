package com.segar.backend.models.DTOs;

public record NotificationDTO(
        Long id,
        String type,
        String title,
        String message,
        String date,
        boolean read
) {}
