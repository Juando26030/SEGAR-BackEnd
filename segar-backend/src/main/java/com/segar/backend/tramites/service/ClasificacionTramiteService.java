package com.segar.backend.tramites.service;

import com.segar.backend.tramites.api.dto.*;
import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO.NivelRiesgo;
import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO.TipoAccion;
import com.segar.backend.tramites.api.dto.DocumentoRequeridoDTO.*;
import com.segar.backend.tramites.api.dto.ResultadoClasificacionDTO.TipoTramiteINVIMA;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Servicio que implementa la lógica de clasificación de trámites INVIMA
 * Con todas las reglas de negocio oficiales del proceso de registro sanitario
 */
@Service
public class ClasificacionTramiteService {

    public String clasificarTramite(String categoria, String poblacion, String procesamiento) {

        categoria = categoria.toLowerCase(Locale.ROOT);
        poblacion = poblacion.toLowerCase(Locale.ROOT);
        procesamiento = procesamiento.toLowerCase(Locale.ROOT);

        // PANADERÍA / GALLETERÍA / CONFITERÍA
		if (categoria.contains("confiteria")) {
			return "NSO"; // BAJO RIESGO - Notificación Sanitaria Obligatoria 
		}
        if (categoria.contains("panadería") || categoria.contains("galletería")) {
            if (procesamiento.contains("horneado") || procesamiento.contains("deshidratado")) {
                return "Bajo - NSO (Notificación Sanitaria Obligatoria)";
            }
            if (procesamiento.contains("rellenos") || procesamiento.contains("cubiertas") || procesamiento.contains("vacío")) {
                if (poblacion.contains("infantil") || poblacion.contains("sensible")) {
                    return "Alto - RSA (Registro Sanitario)";
                } else {
                    return "Medio - PSA (Permiso Sanitario)";
                }
            }
        }

        // LÁCTEOS Y DERIVADOS
        if (categoria.contains("lácteo")) {
            if (procesamiento.contains("pasteurizado") && procesamiento.contains("fermentado") || procesamiento.contains("polvo")) {
                return "Alto - RSA (Registro Sanitario)";
            } else if (procesamiento.contains("pasteurizado") || procesamiento.contains("refrigerado")) {
                return "Medio - PSA (Permiso Sanitario)";
            }
        }

        // PRODUCTOS CÁRNICOS PROCESADOS
        if (categoria.contains("cárnic")) {
            if (procesamiento.contains("listos para consumo") || procesamiento.contains("precocido") || procesamiento.contains("congelado")) {
                return "Alto - RSA (Registro Sanitario)";
            } else if (procesamiento.contains("cocido") || procesamiento.contains("curado")) {
                return "Medio - PSA (Permiso Sanitario)";
            }
        }

        // JUGOS, NÉCTARES, BEBIDAS
        if (categoria.contains("jugo") || categoria.contains("néctar") || categoria.contains("bebida")) {
            if (poblacion.contains("infantil")) {
                return "Alto - RSA (Registro Sanitario)";
            } else {
                return "Medio - PSA (Permiso Sanitario)";
            }
        }

        // CONSERVAS, SALSAS Y ADEREZOS
        if (categoria.contains("conserva") || categoria.contains("salsa") || categoria.contains("aderezo")) {
            if (procesamiento.contains("esterilizado") || procesamiento.contains("atmósfera modificada")) {
                return "Alto - RSA (Registro Sanitario)";
            } else {
                return "Medio - PSA (Permiso Sanitario)";
            }
        }

        return "No clasificado - Información insuficiente";
    }
}

