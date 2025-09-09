package com.segar.backend.shared.events;

import com.segar.backend.tramites.domain.EstadoTramite;

/**
 * Evento de dominio que se publica cuando se crea un trámite
 */
public record TramiteCreadoEvent(
    Long tramiteId,
    String tipoTramite,
    Long empresaId,
    String usuarioCreador
) {}
