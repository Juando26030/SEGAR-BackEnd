package com.segar.backend.shared.events;

import java.util.List;

public record DocumentosSolicitudAsociadosEvent(
        Long solicitudId,
        List<Long> documentosIds
) {}
