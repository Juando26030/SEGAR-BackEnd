package com.segar.backend.tramites.dto;

public record TimelineEventDTO(
        Long id,
        String title,
        String description,
        String date,
        boolean completed,
        boolean current
) {}
