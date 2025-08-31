package com.segar.backend.services.interfaces;

import com.segar.backend.dto.ResolucionDTO;

/**
 * Servicio para gestión de resoluciones INVIMA
 */
public interface ResolucionService {

    ResolucionDTO generarResolucion(Long tramiteId, String decision, String observaciones, String autoridad);

    ResolucionDTO obtenerResolucionPorTramite(Long tramiteId);

    String generarNumeroResolucion();

    void notificarResolucion(Long resolucionId);
}
