package com.segar.backend.exceptions;

/**
 * Excepción lanzada cuando faltan documentos obligatorios para radicar una solicitud
 * Implementada para el Paso 5: Radicación de la Solicitud
 */
public class DocumentosIncompletosException extends RuntimeException {

    public DocumentosIncompletosException(String message) {
        super(message);
    }

    public DocumentosIncompletosException(String message, Throwable cause) {
        super(message, cause);
    }
}
