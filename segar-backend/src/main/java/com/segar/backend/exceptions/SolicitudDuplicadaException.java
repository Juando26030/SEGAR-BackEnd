package com.segar.backend.exceptions;

/**
 * Excepción lanzada cuando ya existe una solicitud radicada para el mismo producto y tipo de trámite
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
