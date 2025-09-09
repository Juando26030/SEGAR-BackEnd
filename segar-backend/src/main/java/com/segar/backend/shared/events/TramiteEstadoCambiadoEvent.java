package com.segar.backend.shared.events;

import com.segar.backend.tramites.domain.EstadoTramite;

/**
 * Evento de dominio que se publica cuando cambia el estado de un trámite
 */
public record TramiteEstadoCambiadoEvent(
    Long tramiteId,
    EstadoTramite estadoAnterior,
    EstadoTramite estadoNuevo,
    String observaciones
) {}
