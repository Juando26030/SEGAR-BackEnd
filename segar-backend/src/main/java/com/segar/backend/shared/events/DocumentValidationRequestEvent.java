package com.segar.backend.shared.events;

import java.util.List;

public record DocumentValidationRequestEvent(
        String requestId,
        Long empresaId,
        List<Long> documentIds,
        String validationType // "OBLIGATORIOS" o "EMPRESA"
) {}
