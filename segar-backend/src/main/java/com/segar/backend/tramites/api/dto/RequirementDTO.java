package com.segar.backend.tramites.api.dto;

public record RequirementDTO(
        Long id,
        String number,
        String title,
        String description,
        int daysRemaining,
        String status,
        String date
) {}
