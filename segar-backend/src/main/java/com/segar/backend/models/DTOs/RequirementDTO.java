package com.segar.backend.models.DTOs;

public record RequirementDTO(
        Long id,
        String number,
        String title,
        String description,
        int daysRemaining,
        String status,
        String date
) {}
