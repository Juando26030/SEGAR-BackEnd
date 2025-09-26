package com.segar.backend.shared.events;

import java.util.List;

public record DocumentValidationResponseEvent(
        String requestId,
        boolean isValid,
        String errorMessage,
        int documentCount,
        List<String> documentDetails
) {}
