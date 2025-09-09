package com.segar.backend.shared.events;

/**
 * Evento de dominio que se publica cuando se crea un requerimiento
 */
public record RequerimientoCreadoEvent(
    Long requerimientoId,
    Long tramiteId,
    String descripcion,
    String usuarioDestino
) {}
