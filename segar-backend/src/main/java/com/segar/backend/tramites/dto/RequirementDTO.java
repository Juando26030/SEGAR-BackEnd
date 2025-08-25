package com.segar.backend.tramites.dto;

public record RequirementDTO(
        Long id,
        String number,
        String title,
        String description,
        int daysRemaining,
        String status,
        String date
) {}
