package com.segar.backend.notificaciones.domain;

/**
 * Estados posibles de un correo electrónico
 */
public enum EmailStatus {
    DRAFT,      // Borrador
    QUEUED,     // En cola para envío
    SENT,       // Enviado exitosamente
    FAILED,     // Falló el envío
    RECEIVED,   // Recibido
    DELETED     // Eliminado
}
