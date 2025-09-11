package com.segar.backend.shared.events;

import com.segar.backend.shared.domain.TipoDocumento;
import java.util.List;

public record DocumentosTipoValidationRequestEvent(
        String requestId,
        List<Long> documentosIds,
        List<TipoDocumento> tiposObligatorios
) {}
