package com.segar.backend.shared.events;

import java.util.List;

public record DocumentosSolicitudUpdateEvent(
        Long solicitudId,
        List<Long> documentosIds
) {}
