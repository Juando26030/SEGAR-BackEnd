package com.segar.backend.notificaciones.domain;

/**
 * Excepción personalizada para errores en la lectura de correos electrónicos
 */
public class EmailReadingException extends Exception {

    private final String errorCode;

    public EmailReadingException(String message) {
        super(message);
        this.errorCode = "EMAIL_READ_ERROR";
    }

    public EmailReadingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "EMAIL_READ_ERROR";
    }

    public EmailReadingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EmailReadingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
