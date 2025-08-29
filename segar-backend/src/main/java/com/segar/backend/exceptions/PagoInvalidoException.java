package com.segar.backend.exceptions;

/**
 * Excepción lanzada cuando no existe un pago válido para radicar una solicitud
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
public class PagoInvalidoException extends RuntimeException {

    public PagoInvalidoException(String message) {
        super(message);
    }

    public PagoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
