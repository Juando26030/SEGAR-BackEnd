package com.segar.backend.tramites.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta radicar una solicitud duplicada
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
public class SolicitudDuplicadaException extends RuntimeException {

    public SolicitudDuplicadaException(String message) {
        super(message);
    }

    public SolicitudDuplicadaException(String message, Throwable cause) {
        super(message, cause);
    }
}
