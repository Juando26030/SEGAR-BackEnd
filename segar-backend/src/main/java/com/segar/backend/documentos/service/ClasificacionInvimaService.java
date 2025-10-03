package com.segar.backend.documentos.service;

import com.segar.backend.documentos.dto.*;
import com.segar.backend.documentos.domain.CategoriaAlimento;
import com.segar.backend.documentos.domain.NivelRiesgo;
import com.segar.backend.documentos.domain.PoblacionObjetivo;
import com.segar.backend.documentos.domain.TipoProcesamiento;
import com.segar.backend.documentos.domain.TipoTramite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para clasificación de productos INVIMA y procesamiento de formularios
 * Implementa la lógica de negocio basada en la matriz de clasificación INVIMA
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClasificacionInvimaService {

    // Constantes para evitar duplicación
    private static final String ESTADO_RADICADO = "RADICADO";
    private static final String ERROR_SERVIDOR = "Error del servidor: ";

    /**
     * Determina el tipo de trámite (NSO/PSA/RSA) según la clasificación del producto
     */
    public ClasificacionInvimaDTO determinarTramite(ClasificacionInvimaRequestDTO request) {
        log.info("Iniciando determinación de trámite para: {}", request.getNombreProducto());

        try {
            // 1. Validar coherencia de la clasificación
            if (!request.esCombinaciValida()) {
                return ClasificacionInvimaDTO.builder()
                        .esClasificacionValida(false)
                        .mensajeValidacion("Clasificación incoherente según matriz INVIMA")
                        .justificacionClasificacion("La combinación de campos no es válida")
                        .build();
            }

            // 2. Determinar trámite según reglas INVIMA
            TipoTramite tramiteResultante = determinarTramitePorReglas(request);

            // 3. Obtener documentos obligatorios
            List<DocumentoRequeridoDTO> documentos = getDocumentosObligatoriosPorTramite(tramiteResultante);

            // 4. Obtener validaciones adicionales
            List<String> validaciones = getValidacionesAdicionales(request);

            // 5. Generar advertencias
            List<String> advertencias = generarAdvertencias(request);

            return ClasificacionInvimaDTO.builder()
                    .tramiteRequerido(tramiteResultante)
                    .descripcionTramite(tramiteResultante.getDescripcion())
                    .tiempoEstimadoDias(tramiteResultante.getTiempoEstimadoDias())
                    .documentosObligatorios(documentos)
                    .formulariosRequeridos(Arrays.asList("Formulario " + tramiteResultante.name()))
                    .validacionesAdicionales(validaciones)
                    .advertencias(advertencias)
                    .tarifaEstimada(calcularTarifaEstimada(tramiteResultante))
                    .justificacionClasificacion(generarJustificacion(request, tramiteResultante))
                    .esClasificacionValida(true)
                    .categoriaAlimento(request.getCategoriaAlimento().getDescripcion())
                    .nivelRiesgo(request.getNivelRiesgo().getDescripcion())
                    .poblacionObjetivo(request.getPoblacionObjetivo().getDescripcion())
                    .tipoProcesamiento(request.getTipoProcesamiento().getDescripcion())
                    .build();

        } catch (Exception e) {
            log.error("Error determinando trámite", e);
            return ClasificacionInvimaDTO.builder()
                    .esClasificacionValida(false)
                    .mensajeValidacion("Error interno: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Obtiene formularios disponibles según el tipo de trámite
     */
    public List<TipoFormularioDTO> getFormulariosDisponibles(String tipoTramite) {
        List<TipoFormularioDTO> formularios = new ArrayList<>();

        if (tipoTramite == null || tipoTramite.equals("NSO")) {
            formularios.add(TipoFormularioDTO.builder()
                    .codigo("NSO")
                    .nombre("Notificación Sanitaria Obligatoria")
                    .descripcion("Para productos de bajo riesgo")
                    .nivelRiesgo("BAJO")
                    .categorias(Arrays.asList("PANADERIA_PASTELERIA", "GALLETERIA_CONFITERIA"))
                    .build());
        }

        if (tipoTramite == null || tipoTramite.equals("PSA")) {
            formularios.add(TipoFormularioDTO.builder()
                    .codigo("PSA")
                    .nombre("Permiso Sanitario")
                    .descripcion("Para productos de riesgo medio")
                    .nivelRiesgo("MEDIO")
                    .categorias(Arrays.asList("DERIVADOS_LACTEOS", "JUGOS_BEBIDAS", "CONSERVAS_FRUTAS_VEGETALES"))
                    .build());
        }

        if (tipoTramite == null || tipoTramite.equals("RSA")) {
            formularios.add(TipoFormularioDTO.builder()
                    .codigo("RSA")
                    .nombre("Registro Sanitario")
                    .descripcion("Para productos de riesgo alto o poblaciones vulnerables")
                    .nivelRiesgo("ALTO")
                    .categorias(Arrays.asList("ALIMENTOS_INFANTILES", "PRODUCTOS_LISTOS_CONSUMO"))
                    .build());
        }

        return formularios;
    }

    /**
     * Procesa formulario NSO
     */
    public FormularioResponseDTO procesarFormularioNSO(FormularioNSODTO formulario) {
        log.info("Procesando formulario NSO para: {}", formulario.getNombreComercial());

        try {
            // 1. Validaciones específicas NSO
            List<String> errores = validarFormularioNSO(formulario);
            
            if (!errores.isEmpty()) {
                return FormularioResponseDTO.builder()
                        .esExitoso(false)
                        .mensaje("Errores de validación en formulario NSO")
                        .errores(errores)
                        .build();
            }

            // 2. Generar número de radicado
            String numeroRadicado = generarNumeroRadicado("NSO");

            // 3. Generar documentos automáticos
            List<DocumentoGeneradoDTO> documentos = generarDocumentosNSO(formulario);

            // 4. Procesar solicitud
            // Aquí se guardaría en base de datos, se enviaría a INVIMA, etc.

            return FormularioResponseDTO.builder()
                    .esExitoso(true)
                    .mensaje("Formulario NSO procesado exitosamente")
                    .numeroRadicado(numeroRadicado)
                    .tipoTramite("NSO")
                    .documentosGenerados(documentos)
                    .estadoTramite(ESTADO_RADICADO)
                    .tiempoEstimadoDias(1)
                    .build();

        } catch (Exception e) {
            log.error("Error procesando formulario NSO", e);
            return FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje("Error interno procesando formulario NSO")
                    .errores(Arrays.asList(ERROR_SERVIDOR + e.getMessage()))
                    .build();
        }
    }

    /**
     * Procesa formulario PSA
     */
    public FormularioResponseDTO procesarFormularioPSA(FormularioPSADTO formulario) {
        log.info("Procesando formulario PSA para: {}", formulario.getNombreComercial());

        try {
            // 1. Validaciones específicas PSA
            List<String> errores = validarFormularioPSA(formulario);
            
            if (!errores.isEmpty()) {
                return FormularioResponseDTO.builder()
                        .esExitoso(false)
                        .mensaje("Errores de validación en formulario PSA")
                        .errores(errores)
                        .build();
            }

            // 2. Generar número de radicado
            String numeroRadicado = generarNumeroRadicado("PSA");

            // 3. Generar documentos automáticos
            List<DocumentoGeneradoDTO> documentos = generarDocumentosPSA(formulario);

            return FormularioResponseDTO.builder()
                    .esExitoso(true)
                    .mensaje("Formulario PSA procesado exitosamente")
                    .numeroRadicado(numeroRadicado)
                    .tipoTramite("PSA")
                    .documentosGenerados(documentos)
                    .estadoTramite(ESTADO_RADICADO)
                    .tiempoEstimadoDias(15)
                    .build();

        } catch (Exception e) {
            log.error("Error procesando formulario PSA", e);
            return FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje("Error interno procesando formulario PSA")
                    .errores(Arrays.asList(ERROR_SERVIDOR + e.getMessage()))
                    .build();
        }
    }

    /**
     * Procesa formulario RSA
     */
    public FormularioResponseDTO procesarFormularioRSA(FormularioRSADTO formulario) {
        log.info("Procesando formulario RSA para: {}", formulario.getNombreComercial());

        try {
            // 1. Validaciones específicas RSA
            List<String> errores = validarFormularioRSA(formulario);
            
            if (!errores.isEmpty()) {
                return FormularioResponseDTO.builder()
                        .esExitoso(false)
                        .mensaje("Errores de validación en formulario RSA")
                        .errores(errores)
                        .build();
            }

            // 2. Generar número de radicado
            String numeroRadicado = generarNumeroRadicado("RSA");

            // 3. Generar documentos automáticos
            List<DocumentoGeneradoDTO> documentos = generarDocumentosRSA(formulario);

            return FormularioResponseDTO.builder()
                    .esExitoso(true)
                    .mensaje("Formulario RSA procesado exitosamente")
                    .numeroRadicado(numeroRadicado)
                    .tipoTramite("RSA")
                    .documentosGenerados(documentos)
                    .estadoTramite(ESTADO_RADICADO)
                    .tiempoEstimadoDias(45)
                    .build();

        } catch (Exception e) {
            log.error("Error procesando formulario RSA", e);
            return FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje("Error interno procesando formulario RSA")
                    .errores(Arrays.asList(ERROR_SERVIDOR + e.getMessage()))
                    .build();
        }
    }

    // Métodos auxiliares

    private TipoTramite determinarTramitePorReglas(ClasificacionInvimaRequestDTO request) {
        // Población sensible siempre requiere RSA
        if (request.getPoblacionObjetivo().esPoblacionSensible()) {
            return TipoTramite.RSA;
        }

        // Según nivel de riesgo
        return switch (request.getNivelRiesgo()) {
            case BAJO -> TipoTramite.NSO;
            case MEDIO -> TipoTramite.PSA;
            case ALTO -> TipoTramite.RSA;
        };
    }

    private List<DocumentoRequeridoDTO> getDocumentosObligatoriosPorTramite(TipoTramite tramite) {
        
        List<DocumentoRequeridoDTO> documentos = new ArrayList<>();

        // Documentos base requeridos para todos los trámites
        documentos.add(DocumentoRequeridoDTO.builder()
                .nombre("Certificado de existencia y representación legal")
                .descripcion("Expedido por Cámara de Comercio, vigencia máxima 30 días")
                .esObligatorio(true)
                .esGenerableEnApp(false)
                .observaciones("Descargar de Cámara de Comercio")
                .build());

        documentos.add(DocumentoRequeridoDTO.builder()
                .nombre("Ficha técnica del producto")
                .descripcion("Documento técnico con especificaciones del producto")
                .esObligatorio(true)
                .esGenerableEnApp(true)
                .observaciones("Se puede generar automáticamente en la aplicación")
                .build());

        // Documentos específicos según trámite
        if (tramite == TipoTramite.PSA || tramite == TipoTramite.RSA) {
            documentos.add(DocumentoRequeridoDTO.builder()
                    .nombre("Análisis fisicoquímico")
                    .descripcion("Análisis de laboratorio acreditado")
                    .esObligatorio(true)
                    .esGenerableEnApp(false)
                    .observaciones("Debe ser realizado por laboratorio acreditado ante IDEAM")
                    .build());

            documentos.add(DocumentoRequeridoDTO.builder()
                    .nombre("Análisis microbiológico")
                    .descripcion("Análisis microbiológico de laboratorio acreditado")
                    .esObligatorio(true)
                    .esGenerableEnApp(false)
                    .observaciones("Incluir análisis según categoría del producto")
                    .build());
        }

        if (tramite == TipoTramite.RSA) {
            documentos.add(DocumentoRequeridoDTO.builder()
                    .nombre("Estudios de estabilidad y vida útil")
                    .descripcion("Estudios que demuestren la vida útil declarada")
                    .esObligatorio(true)
                    .esGenerableEnApp(false)
                    .observaciones("Estudios acelerados o tiempo real")
                    .build());

            documentos.add(DocumentoRequeridoDTO.builder()
                    .nombre("Plan HACCP implementado")
                    .descripcion("Plan de Análisis de Peligros y Puntos Críticos de Control")
                    .esObligatorio(true)
                    .esGenerableEnApp(true)
                    .observaciones("Se puede generar plantilla en la aplicación")
                    .build());
        }

        return documentos;
    }

    private List<String> getValidacionesAdicionales(ClasificacionInvimaRequestDTO request) {
        List<String> validaciones = new ArrayList<>();

        if (request.getNivelRiesgo().requiereBPM()) {
            validaciones.add("Certificación BPM vigente del establecimiento");
        }

        if (request.getNivelRiesgo().requiereHACCP()) {
            validaciones.add("Sistema HACCP implementado y validado");
        }

        if (request.getTipoProcesamiento().requiereValidacionTermica()) {
            validaciones.add("Validación de proceso térmico");
        }

        if (request.getPoblacionObjetivo().requiereEstudiosNutricionales()) {
            validaciones.add("Estudios nutricionales específicos para la población objetivo");
        }

        return validaciones;
    }

    private List<String> generarAdvertencias(ClasificacionInvimaRequestDTO request) {
        List<String> advertencias = new ArrayList<>();

        String advertenciaEtiqueta = request.getPoblacionObjetivo().getAdvertenciasEtiquetado();
        if (advertenciaEtiqueta != null) {
            advertencias.add("Advertencia obligatoria en etiqueta: " + advertenciaEtiqueta);
        }

        if (request.getEsImportado() != null && request.getEsImportado()) {
            advertencias.add("Producto importado requiere documentación adicional del país de origen");
        }

        return advertencias;
    }

    private String calcularTarifaEstimada(TipoTramite tramite) {
        return switch (tramite) {
            case NSO -> "Aprox. $150,000 - $300,000 COP";
            case PSA -> "Aprox. $500,000 - $1,200,000 COP";
            case RSA -> "Aprox. $1,500,000 - $3,000,000 COP";
            default -> "Consultar tarifa vigente en INVIMA";
        };
    }

    private String generarJustificacion(ClasificacionInvimaRequestDTO request, TipoTramite tramite) {
        StringBuilder justificacion = new StringBuilder();
        
        justificacion.append("Según la clasificación INVIMA: ");
        justificacion.append("Categoría ").append(request.getCategoriaAlimento().getDescripcion());
        justificacion.append(" con nivel de riesgo ").append(request.getNivelRiesgo().getDescripcion());
        justificacion.append(" dirigido a ").append(request.getPoblacionObjetivo().getDescripcion());
        justificacion.append(" y procesamiento ").append(request.getTipoProcesamiento().getDescripcion());
        justificacion.append(" requiere trámite ").append(tramite.getDescripcion());

        return justificacion.toString();
    }

    private String generarNumeroRadicado(String tipoTramite) {
        return String.format("%s-%d-%06d", 
                tipoTramite, 
                LocalDateTime.now().getYear(), 
                System.currentTimeMillis() % 1000000);
    }

    // Métodos de validación (simplificados para el ejemplo)
    private List<String> validarFormularioNSO(FormularioNSODTO formulario) {
        List<String> errores = new ArrayList<>();
        
        if (formulario.getRazonSocial() == null || formulario.getRazonSocial().trim().isEmpty()) {
            errores.add("La razón social es obligatoria");
        }
        
        if (formulario.getNombreComercial() == null || formulario.getNombreComercial().trim().isEmpty()) {
            errores.add("El nombre comercial del producto es obligatorio");
        }

        return errores;
    }

    private List<String> validarFormularioPSA(FormularioPSADTO formulario) {
        List<String> errores = validarFormularioNSO(new FormularioNSODTO()); // Hereda validaciones NSO
        
        if (formulario.getCertificacionBPM() == null) {
            errores.add("La certificación BPM es obligatoria para PSA");
        }

        if (formulario.getAnalisisFisicoquimicos() == null || formulario.getAnalisisFisicoquimicos().isEmpty()) {
            errores.add("Los análisis fisicoquímicos son obligatorios para PSA");
        }

        return errores;
    }

    private List<String> validarFormularioRSA(FormularioRSADTO formulario) {
        List<String> errores = validarFormularioPSA(new FormularioPSADTO()); // Hereda validaciones PSA
        
        if (formulario.getSistemaHACCP() == null) {
            errores.add("El sistema HACCP es obligatorio para RSA");
        }

        if (formulario.getEstudiosEstabilidad() == null || formulario.getEstudiosEstabilidad().isEmpty()) {
            errores.add("Los estudios de estabilidad son obligatorios para RSA");
        }

        return errores;
    }

    // Métodos de generación de documentos (simplificados)
    private List<DocumentoGeneradoDTO> generarDocumentosNSO(FormularioNSODTO formulario) {
        List<DocumentoGeneradoDTO> documentos = new ArrayList<>();
        
        documentos.add(DocumentoGeneradoDTO.builder()
                .nombre("Ficha técnica - " + formulario.getNombreComercial())
                .tipo("PDF")
                .url("/documentos/fichas-tecnicas/" + formulario.getNombreComercial() + ".pdf")
                .contenido("Ficha técnica generada automáticamente")
                .build());

        return documentos;
    }

    private List<DocumentoGeneradoDTO> generarDocumentosPSA(FormularioPSADTO formulario) {
        List<DocumentoGeneradoDTO> documentos = generarDocumentosNSO(new FormularioNSODTO());
        
        documentos.add(DocumentoGeneradoDTO.builder()
                .nombre("Plan BPM - " + formulario.getNombreComercial())
                .tipo("PDF")
                .url("/documentos/planes-bpm/" + formulario.getNombreComercial() + ".pdf")
                .contenido("Plan BPM generado automáticamente")
                .build());

        return documentos;
    }

    private List<DocumentoGeneradoDTO> generarDocumentosRSA(FormularioRSADTO formulario) {
        List<DocumentoGeneradoDTO> documentos = generarDocumentosPSA(new FormularioPSADTO());
        
        documentos.add(DocumentoGeneradoDTO.builder()
                .nombre("Plan HACCP - " + formulario.getNombreComercial())
                .tipo("PDF")
                .url("/documentos/planes-haccp/" + formulario.getNombreComercial() + ".pdf")
                .contenido("Plan HACCP generado automáticamente")
                .build());

        return documentos;
    }

    // Métodos adicionales requeridos por el controlador
    public ValidacionClasificacionDTO validarClasificacion(String categoria, String riesgo, String poblacion, String procesamiento) {
        // Implementación básica de validación - parámetros usados para validación futura
        log.debug("Validando clasificación: {} - {} - {} - {}", categoria, riesgo, poblacion, procesamiento);
        return ValidacionClasificacionDTO.builder()
                .esValida(true)
                .mensaje("Clasificación válida")
                .tramiteSugerido("NSO")
                .build();
    }

    public List<DocumentoRequeridoDTO> getDocumentosRequeridos(String tipoTramite, String categoria, String poblacion) {
        // Implementación básica - parámetros para lógica futura
        log.debug("Obteniendo documentos para: {} - {} - {}", tipoTramite, categoria, poblacion);
        return new ArrayList<>();
    }

    public MatrizClasificacionDTO getMatrizClasificacion() {
        // Implementación básica de la matriz
        return MatrizClasificacionDTO.builder()
                .categorias(Arrays.asList("PANADERIA_PASTELERIA", "DERIVADOS_LACTEOS"))
                .nivelesRiesgo(Arrays.asList("BAJO", "MEDIO", "ALTO"))
                .poblaciones(Arrays.asList("POBLACION_GENERAL", "ALIMENTACION_INFANTIL"))
                .procesamientos(Arrays.asList("HORNEADO", "PASTEURIZADO", "CONGELADO"))
                .escenarios(new ArrayList<>())
                .build();
    }

    public DocumentosGeneradosDTO generarDocumentosAutomaticos(String tipoFormulario, Object datosFormulario) {
        // Parámetros para generación de documentos específicos
        log.debug("Generando documentos para tipo: {}", tipoFormulario);
        log.trace("Datos del formulario: {}", datosFormulario);
        return DocumentosGeneradosDTO.builder()
                .esExitoso(true)
                .mensaje("Documentos generados exitosamente")
                .documentos(new ArrayList<>())
                .build();
    }
}