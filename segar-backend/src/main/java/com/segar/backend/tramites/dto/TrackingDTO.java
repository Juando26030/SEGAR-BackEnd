package com.segar.backend.tramites.dto;

import java.time.LocalDate;

public record TrackingDTO(
        String radicadoNumber,
        LocalDate submissionDate,
        String procedureType,
        String productName,
        String currentStatus,
        long daysElapsed
) {}
