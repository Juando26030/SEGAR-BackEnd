package com.segar.backend.shared.domain;

/**
 * Estados posibles de un trámite
 */
public enum EstadoTramite {
    RADICADO("Radicado"),
    EN_EVALUACION_TECNICA("En Evaluación Técnica"),
    REQUIERE_INFORMACION("Requiere Información"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");

    private final String descripcion;

    EstadoTramite(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
