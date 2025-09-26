package com.segar.backend.tramites.api.dto;

public record TimelineEventDTO(
        Long id,
        String title,
        String description,
        String date,
        boolean completed,
        boolean currentEvent
) {}
