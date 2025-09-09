package com.segar.backend.shared.domain;

/**
 * Estados posibles de una solicitud
 */
public enum EstadoSolicitud {
    BORRADOR("Borrador"),
    RADICADA("Radicada"),
    EN_EVALUACION("En Evaluación"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    FINALIZADA("Finalizada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
