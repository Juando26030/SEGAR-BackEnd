package com.segar.backend.models;

/**
 * Estados posibles de una solicitud en el sistema SEGAR
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public enum EstadoSolicitud {
    /**
     * Solicitud en borrador, aún no completada
     */
    BORRADOR("Borrador"),

    /**
     * Solicitud pendiente de radicación
     */
    PENDIENTE("Pendiente"),

    /**
     * Solicitud radicada formalmente ante INVIMA
     */
    RADICADA("Radicada"),

    /**
     * Solicitud rechazada por documentación incompleta o errores
     */
    RECHAZADA("Rechazada"),

    /**
     * Solicitud aprobada y registro sanitario otorgado
     */
    APROBADA("Aprobada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
