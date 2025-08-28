package com.segar.backend.models.DTOs;

public record TimelineEventDTO(
        Long id,
        String title,
        String description,
        String date,
        boolean completed,
        boolean current
) {}
