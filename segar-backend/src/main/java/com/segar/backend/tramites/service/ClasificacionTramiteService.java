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

/**
 * Servicio que implementa la lógica de clasificación de trámites INVIMA
 * Con todas las reglas de negocio oficiales del proceso de registro sanitario
 */
@Service
public class ClasificacionTramiteService {

    // Poblaciones vulnerables según normativa INVIMA
    private static final List<String> POBLACIONES_VULNERABLES = Arrays.asList(
            "infantil", "gestantes", "gestante", "adultos mayores", "adulto mayor",
            "tercera-edad", "especial", "bebés", "bebes", "niños", "ninos"
    );

    // Procesamientos que elevan automáticamente a RSA
    private static final List<String> PROCESAMIENTOS_ALTO_RIESGO = Arrays.asList(
            "esterilizado", "esterilizacion", "atmósfera modificada", "atmosfera modificada",
            "congelado", "congelación", "congelacion", "ultra congelado"
    );

    // Categorías con riesgo inherente alto
    private static final List<String> CATEGORIAS_RIESGO_ALTO = Arrays.asList(
            "lacteos", "lácteos", "carnicos", "cárnicos", "productos carnicos",
            "derivados carnicos", "derivados lácteos"
    );

    /**
     * Clasifica un producto y determina el tipo de trámite según reglas INVIMA
     */
    public ResultadoClasificacionDTO clasificarProducto(ClasificacionProductoDTO clasificacion) {
        // 1. Determinar tipo de trámite aplicando todas las reglas
        TipoTramiteINVIMA tipoTramite = determinarTipoTramiteConReglas(clasificacion);

        // 2. Generar documentos según tipo de trámite
        List<DocumentoRequeridoDTO> documentos = generarDocumentosRequeridos(
                tipoTramite, clasificacion
        );

        // 3. Generar advertencias contextuales
        List<String> advertencias = generarAdvertencias(tipoTramite, clasificacion);

        // 4. Determinar tiempos y costos
        String tiempoEstimado = obtenerTiempoEstimado(tipoTramite);
        String costoEstimado = obtenerCostoEstimado(tipoTramite);

        return ResultadoClasificacionDTO.builder()
                .tramite(tipoTramite)
                .tramiteDescripcion(obtenerDescripcionTramite(tipoTramite))
                .documentos(documentos)
                .advertencias(advertencias)
                .tiempoEstimado(tiempoEstimado)
                .costoEstimado(costoEstimado)
                .build();
    }

    /**
     * Determina el tipo de trámite aplicando TODAS las reglas de negocio INVIMA
     * en orden de prioridad
     */
    private TipoTramiteINVIMA determinarTipoTramiteConReglas(ClasificacionProductoDTO clasificacion) {
        // REGLA 1: Población vulnerable → RSA (MÁXIMA PRIORIDAD)
        if (esPoblacionVulnerable(clasificacion.getPoblacionObjetivo())) {
            return TipoTramiteINVIMA.RSA;
        }

        // REGLA 2: Procesamiento de alto riesgo → RSA
        if (esProcesamientoAltoRiesgo(clasificacion.getProcesamiento())) {
            return TipoTramiteINVIMA.RSA;
        }

        // REGLA 3: Riesgo alto explícito → RSA
        if (clasificacion.getNivelRiesgo() == NivelRiesgo.ALTO) {
            return TipoTramiteINVIMA.RSA;
        }

        // REGLA 4: Categoría de riesgo inherente alto + riesgo medio → RSA
        if (clasificacion.getNivelRiesgo() == NivelRiesgo.MEDIO &&
            esCategoriaRiesgoAlto(clasificacion.getCategoria())) {
            return TipoTramiteINVIMA.RSA;
        }

        // REGLA 5: Producto importado + riesgo medio o alto → Mínimo PSA
        if (Boolean.TRUE.equals(clasificacion.getEsImportado())) {
            // Si ya tiene riesgo alto, seguirá siendo RSA por reglas anteriores
            // Si tiene riesgo medio, mínimo PSA
            if (clasificacion.getNivelRiesgo() == NivelRiesgo.MEDIO) {
                return TipoTramiteINVIMA.PSA;
            }
        }

        // REGLA 6: Riesgo medio → PSA
        if (clasificacion.getNivelRiesgo() == NivelRiesgo.MEDIO) {
            return TipoTramiteINVIMA.PSA;
        }

        // REGLA 7: Riesgo bajo + población general → NSO
        if (clasificacion.getNivelRiesgo() == NivelRiesgo.BAJO) {
            return TipoTramiteINVIMA.NSO;
        }

        // REGLA POR DEFECTO: NSO (caso más permisivo)
        return TipoTramiteINVIMA.NSO;
    }

    /**
     * Genera la lista de documentos requeridos según el tipo de trámite
     */
    private List<DocumentoRequeridoDTO> generarDocumentosRequeridos(
            TipoTramiteINVIMA tipoTramite,
            ClasificacionProductoDTO clasificacion) {

        List<DocumentoRequeridoDTO> documentos = new ArrayList<>();

        // Documentos básicos (NSO) - Todos los trámites los requieren
        documentos.addAll(getDocumentosNSO());

        // Documentos adicionales para PSA
        if (tipoTramite == TipoTramiteINVIMA.PSA || tipoTramite == TipoTramiteINVIMA.RSA) {
            documentos.addAll(getDocumentosPSA());
        }

        // Documentos adicionales para RSA
        if (tipoTramite == TipoTramiteINVIMA.RSA) {
            documentos.addAll(getDocumentosRSA(clasificacion));
        }

        // Documentos para importados
        if (Boolean.TRUE.equals(clasificacion.getEsImportado())) {
            documentos.addAll(getDocumentosImportados());
        }

        // Documentos para renovación/modificación
        if (clasificacion.getTipoAccion() == TipoAccion.RENOVACION ||
            clasificacion.getTipoAccion() == TipoAccion.MODIFICACION) {
            documentos.addAll(getDocumentosRenovacion());
        }

        return documentos;
    }

    /**
     * Documentos básicos para NSO (5 documentos)
     */
    private List<DocumentoRequeridoDTO> getDocumentosNSO() {
        List<DocumentoRequeridoDTO> docs = new ArrayList<>();

        // 1. Certificado de Existencia
        docs.add(DocumentoRequeridoDTO.builder()
                .id("certificado_existencia")
                .nombre("Certificado de Existencia y Representación Legal")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Certificado expedido por la Cámara de Comercio con vigencia no mayor a 30 días")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(true)
                .orden(1)
                .icono("building")
                .campos(Arrays.asList(
                        crearCampo("razon_social", "text", true, "Razón social de la empresa"),
                        crearCampo("nit", "text", true, "NIT sin dígito de verificación"),
                        crearCampo("representante_legal", "text", true, "Nombre completo"),
                        crearCampo("fecha_expedicion", "date", true, "No mayor a 30 días"),
                        crearCampo("archivo", "file", true, "Formato PDF")
                ))
                .build());

        // 2. Ficha Técnica Básica
        docs.add(DocumentoRequeridoDTO.builder()
                .id("ficha_tecnica_basica")
                .nombre("Ficha Técnica Básica")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Información básica del producto alimenticio")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(true)
                .orden(2)
                .icono("file-alt")
                .campos(Arrays.asList(
                        crearCampo("nombre_comercial", "text", true, "Nombre del producto"),
                        crearCampo("marca", "text", true, "Marca registrada"),
                        crearCampo("denominacion", "text", true, "Denominación del alimento"),
                        crearCampo("presentacion", "text", true, "Ej: Vaso x 150g"),
                        crearCampo("vida_util", "number", true, "Días de vida útil"),
                        crearCampo("condiciones_conservacion", "textarea", true, "Condiciones de almacenamiento"),
                        crearCampo("ingredientes", "textarea", true, "Lista completa en orden descendente"),
                        crearCampo("aditivos", "textarea", false, "Aditivos utilizados"),
                        crearCampo("alergenos", "textarea", true, "Advertencias de alérgenos")
                ))
                .build());

        // 3. Etiqueta Digital
        docs.add(DocumentoRequeridoDTO.builder()
                .id("etiqueta_digital")
                .nombre("Etiqueta Digital o Diseño de Rotulado")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.IMAGE)
                .descripcion("Etiqueta del producto según Resolución 5109 de 2005")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(true)
                .orden(3)
                .icono("tag")
                .campos(Arrays.asList(
                        crearCampo("nombre_producto", "text", true, "Nombre en etiqueta"),
                        crearCampo("lista_ingredientes", "textarea", true, "Ingredientes en etiqueta"),
                        crearCampo("tabla_nutricional", "text", true, "Información nutricional"),
                        crearCampo("archivo", "file", true, "Formato imagen o PDF")
                ))
                .build());

        // 4. Comprobante de Pago
        docs.add(DocumentoRequeridoDTO.builder()
                .id("comprobante_pago")
                .nombre("Comprobante de Pago INVIMA")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Comprobante de pago de derechos de trámite")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(true)
                .orden(4)
                .icono("money-bill")
                .campos(Arrays.asList(
                        crearCampo("codigo_recaudo", "text", true, "Código de recaudo"),
                        crearCampo("monto", "number", true, "Valor pagado"),
                        crearCampo("fecha_pago", "date", true, "Fecha del pago"),
                        crearCampo("archivo", "file", true, "Comprobante PDF")
                ))
                .build());

        // 5. Poder (Opcional)
        docs.add(DocumentoRequeridoDTO.builder()
                .id("poder_representacion")
                .nombre("Poder de Representación")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Poder si la solicitud la presenta un apoderado")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(false)
                .orden(5)
                .icono("user-tie")
                .campos(Arrays.asList(
                        crearCampo("otorgante", "text", false, "Quien otorga el poder"),
                        crearCampo("apoderado", "text", false, "Quien recibe el poder"),
                        crearCampo("objeto", "textarea", false, "Objeto del poder"),
                        crearCampo("ciudad", "text", false, "Ciudad de otorgamiento")
                ))
                .build());

        return docs;
    }

    /**
     * Documentos adicionales para PSA (5 documentos)
     */
    private List<DocumentoRequeridoDTO> getDocumentosPSA() {
        List<DocumentoRequeridoDTO> docs = new ArrayList<>();

        // 6. Análisis Fisicoquímico
        docs.add(DocumentoRequeridoDTO.builder()
                .id("analisis_fisicoquimico")
                .nombre("Análisis Fisicoquímico")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Análisis realizado por laboratorio acreditado ante el ONAC")
                .categoria(CategoriaDocumento.ANALISIS)
                .obligatorio(true)
                .orden(10)
                .icono("flask")
                .campos(Arrays.asList(
                        crearCampo("ph", "number", true, "Valor de pH"),
                        crearCampo("humedad", "number", true, "Porcentaje de humedad"),
                        crearCampo("azucares", "number", false, "Contenido de azúcares"),
                        crearCampo("actividad_agua", "number", false, "Actividad de agua (aw)"),
                        crearCampo("laboratorio", "text", true, "Nombre del laboratorio acreditado"),
                        crearCampo("fecha_ensayo", "date", true, "Fecha del análisis"),
                        crearCampo("archivo", "file", true, "Certificado del laboratorio")
                ))
                .build());

        // 7. Análisis Microbiológico
        docs.add(DocumentoRequeridoDTO.builder()
                .id("analisis_microbiologico")
                .nombre("Análisis Microbiológico")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Análisis microbiológico por laboratorio acreditado")
                .categoria(CategoriaDocumento.ANALISIS)
                .obligatorio(true)
                .orden(11)
                .icono("microscope")
                .campos(Arrays.asList(
                        crearCampo("coliformes_totales", "text", true, "Resultado coliformes totales"),
                        crearCampo("e_coli", "text", true, "Resultado E. coli"),
                        crearCampo("salmonella", "text", true, "Resultado Salmonella spp."),
                        crearCampo("mohos", "text", false, "Recuento de mohos"),
                        crearCampo("levaduras", "text", false, "Recuento de levaduras"),
                        crearCampo("laboratorio", "text", true, "Laboratorio emisor"),
                        crearCampo("fecha_ensayo", "date", true, "Fecha del análisis"),
                        crearCampo("archivo", "file", true, "Certificado del laboratorio")
                ))
                .build());

        // 8. Certificación BPM
        docs.add(DocumentoRequeridoDTO.builder()
                .id("certificacion_bpm")
                .nombre("Certificación BPM del Establecimiento")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Certificación de Buenas Prácticas de Manufactura vigente")
                .categoria(CategoriaDocumento.CERTIFICACION)
                .obligatorio(true)
                .orden(12)
                .icono("certificate")
                .campos(Arrays.asList(
                        crearCampo("nombre_establecimiento", "text", true, "Nombre de la planta"),
                        crearCampo("numero_registro", "text", true, "Número de registro sanitario"),
                        crearCampo("fecha_expedicion", "date", true, "Fecha de expedición"),
                        crearCampo("vigencia", "date", true, "Fecha de vencimiento"),
                        crearCampo("autoridad_emisora", "text", true, "INVIMA"),
                        crearCampo("archivo", "file", true, "Certificado PDF")
                ))
                .build());

        // 9. Ficha Técnica Detallada
        docs.add(DocumentoRequeridoDTO.builder()
                .id("ficha_tecnica_detallada")
                .nombre("Ficha Técnica Detallada")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Ficha técnica completa con valores fisicoquímicos")
                .categoria(CategoriaDocumento.BASICO)
                .obligatorio(true)
                .orden(13)
                .icono("file-medical")
                .campos(Arrays.asList(
                        crearCampo("valores_fisicoquimicos", "textarea", true, "pH, humedad, etc."),
                        crearCampo("composicion_nutricional", "textarea", true, "Tabla nutricional completa"),
                        crearCampo("especificaciones_tecnicas", "textarea", true, "Especificaciones del producto"),
                        crearCampo("vida_util_detallada", "textarea", true, "Estudios de vida útil")
                ))
                .build());

        // 10. Plan BPM
        docs.add(DocumentoRequeridoDTO.builder()
                .id("plan_bpm")
                .nombre("Plan BPM")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Plan de Buenas Prácticas de Manufactura del establecimiento")
                .categoria(CategoriaDocumento.CERTIFICACION)
                .obligatorio(true)
                .orden(14)
                .icono("clipboard-check")
                .campos(Arrays.asList(
                        crearCampo("limpieza_desinfeccion", "textarea", true, "Programa de limpieza"),
                        crearCampo("control_plagas", "textarea", true, "Control integrado de plagas"),
                        crearCampo("manejo_agua", "textarea", true, "Manejo de agua potable"),
                        crearCampo("manejo_residuos", "textarea", true, "Gestión de residuos"),
                        crearCampo("higiene_personal", "textarea", true, "Prácticas de higiene"),
                        crearCampo("mantenimiento", "textarea", true, "Plan de mantenimiento"),
                        crearCampo("trazabilidad", "textarea", true, "Sistema de trazabilidad")
                ))
                .build());

        return docs;
    }

    /**
     * Documentos adicionales para RSA (6+ documentos)
     */
    private List<DocumentoRequeridoDTO> getDocumentosRSA(ClasificacionProductoDTO clasificacion) {
        List<DocumentoRequeridoDTO> docs = new ArrayList<>();

        // 11. Estudios de Estabilidad
        docs.add(DocumentoRequeridoDTO.builder()
                .id("estudios_estabilidad")
                .nombre("Estudios de Estabilidad y Vida Útil")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Estudios de estabilidad del producto en el tiempo")
                .categoria(CategoriaDocumento.ESTUDIOS)
                .obligatorio(true)
                .orden(20)
                .icono("chart-line")
                .campos(Arrays.asList(
                        crearCampo("producto", "text", true, "Nombre del producto"),
                        crearCampo("lotes_evaluados", "text", true, "Lotes utilizados"),
                        crearCampo("condiciones_almacenamiento", "textarea", true, "Condiciones del estudio"),
                        crearCampo("temperatura", "text", true, "Temperatura de almacenamiento"),
                        crearCampo("tiempo_evaluacion", "number", true, "Días evaluados"),
                        crearCampo("parametros_evaluados", "textarea", true, "Parámetros fisicoquímicos y microbiológicos"),
                        crearCampo("vida_util_determinada", "number", true, "Vida útil en días"),
                        crearCampo("archivo", "file", true, "Estudio completo PDF")
                ))
                .build());

        // 12. Certificación HACCP
        docs.add(DocumentoRequeridoDTO.builder()
                .id("certificacion_haccp")
                .nombre("Certificación HACCP")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Certificación de Análisis de Peligros y Puntos Críticos de Control")
                .categoria(CategoriaDocumento.CERTIFICACION)
                .obligatorio(true)
                .orden(21)
                .icono("shield-alt")
                .campos(Arrays.asList(
                        crearCampo("empresa", "text", true, "Nombre de la empresa"),
                        crearCampo("codigo_certificacion", "text", true, "Código de certificación"),
                        crearCampo("fecha_emision", "date", true, "Fecha de emisión"),
                        crearCampo("vigencia", "date", true, "Fecha de vencimiento"),
                        crearCampo("organismo_certificador", "text", true, "Entidad certificadora"),
                        crearCampo("archivo", "file", true, "Certificado PDF")
                ))
                .build());

        // 13. Plan HACCP
        docs.add(DocumentoRequeridoDTO.builder()
                .id("plan_haccp")
                .nombre("Plan HACCP Completo")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Plan completo de HACCP del producto")
                .categoria(CategoriaDocumento.CERTIFICACION)
                .obligatorio(true)
                .orden(22)
                .icono("tasks")
                .campos(Arrays.asList(
                        crearCampo("diagrama_flujo", "textarea", true, "Diagrama de flujo del proceso"),
                        crearCampo("puntos_criticos_control", "textarea", true, "PCC identificados"),
                        crearCampo("limites_criticos", "textarea", true, "Límites críticos por PCC"),
                        crearCampo("monitoreo", "textarea", true, "Sistema de monitoreo"),
                        crearCampo("medidas_correctivas", "textarea", true, "Acciones correctivas"),
                        crearCampo("responsables", "textarea", true, "Responsables de cada PCC")
                ))
                .build());

        // 14. Certificado BPM INVIMA Vigente
        docs.add(DocumentoRequeridoDTO.builder()
                .id("certificado_bpm_invima_vigente")
                .nombre("Certificado BPM INVIMA Vigente")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Certificado BPM específico de INVIMA con vigencia actual")
                .categoria(CategoriaDocumento.CERTIFICACION)
                .obligatorio(true)
                .orden(23)
                .icono("stamp")
                .campos(Arrays.asList(
                        crearCampo("numero_certificado_invima", "text", true, "Número de certificado INVIMA"),
                        crearCampo("fecha_expedicion", "date", true, "Fecha de expedición"),
                        crearCampo("vigencia", "date", true, "Fecha de vencimiento"),
                        crearCampo("establecimiento", "text", true, "Nombre del establecimiento"),
                        crearCampo("archivo", "file", true, "Certificado PDF")
                ))
                .build());

        // 15. Estudios Nutricionales (si aplica población vulnerable)
        if (esPoblacionVulnerable(clasificacion.getPoblacionObjetivo())) {
            docs.add(DocumentoRequeridoDTO.builder()
                    .id("estudios_nutricionales")
                    .nombre("Estudios Nutricionales Especializados")
                    .tipo(TipoDocumentoRequerido.EXTERNO)
                    .formato(FormatoDocumento.PDF)
                    .descripcion("Estudios nutricionales para población vulnerable")
                    .categoria(CategoriaDocumento.ESTUDIOS)
                    .obligatorio(true)
                    .orden(24)
                    .icono("heartbeat")
                    .campos(Arrays.asList(
                            crearCampo("composicion_completa", "textarea", true, "Macros y micronutrientes"),
                            crearCampo("energia", "number", true, "Valor energético"),
                            crearCampo("proteinas", "number", true, "Gramos de proteína"),
                            crearCampo("grasas", "number", true, "Gramos de grasa"),
                            crearCampo("carbohidratos", "number", true, "Gramos de carbohidratos"),
                            crearCampo("vitaminas_minerales", "textarea", true, "Contenido de vitaminas y minerales"),
                            crearCampo("comparativo_normativa", "textarea", true, "Comparación con normas"),
                            crearCampo("laboratorio", "text", true, "Laboratorio que realizó el estudio"),
                            crearCampo("archivo", "file", true, "Estudio completo PDF")
                    ))
                    .build());
        }

        // 16. Advertencias Obligatorias en Etiqueta
        docs.add(DocumentoRequeridoDTO.builder()
                .id("advertencias_etiquetado")
                .nombre("Advertencias Obligatorias en Etiqueta")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Advertencias especiales para población vulnerable")
                .categoria(CategoriaDocumento.OTROS)
                .obligatorio(true)
                .orden(25)
                .icono("exclamation-triangle")
                .campos(Arrays.asList(
                        crearCampo("advertencia_principal", "textarea", true,
                                "Ej: La lactancia materna es el mejor alimento para el niño"),
                        crearCampo("modo_preparacion", "textarea", true, "Instrucciones de preparación"),
                        crearCampo("advertencias_adicionales", "textarea", false, "Otras advertencias")
                ))
                .build());

        // 17. Protocolo de Estabilidad
        docs.add(DocumentoRequeridoDTO.builder()
                .id("protocolo_estabilidad")
                .nombre("Protocolo de Estabilidad")
                .tipo(TipoDocumentoRequerido.AUTOGENERADO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Protocolo detallado de estudios de estabilidad")
                .categoria(CategoriaDocumento.ESTUDIOS)
                .obligatorio(true)
                .orden(26)
                .icono("file-contract")
                .campos(Arrays.asList(
                        crearCampo("condiciones", "textarea", true, "Condiciones del protocolo"),
                        crearCampo("parametros_control", "textarea", true, "Parámetros a controlar"),
                        crearCampo("lotes", "text", true, "Lotes a evaluar"),
                        crearCampo("frecuencia_muestreo", "text", true, "Frecuencia de muestreo"),
                        crearCampo("resultados", "textarea", false, "Resultados obtenidos")
                ))
                .build());

        return docs;
    }

    /**
     * Documentos para productos importados (3 documentos)
     */
    private List<DocumentoRequeridoDTO> getDocumentosImportados() {
        List<DocumentoRequeridoDTO> docs = new ArrayList<>();

        docs.add(DocumentoRequeridoDTO.builder()
                .id("certificado_venta_libre")
                .nombre("Certificado de Venta Libre del País de Origen")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Documento que certifica la libre venta del producto en su país de origen")
                .categoria(CategoriaDocumento.OTROS)
                .obligatorio(true)
                .orden(30)
                .icono("globe")
                .campos(Arrays.asList(
                        crearCampo("pais_origen", "text", true, "País de origen del producto"),
                        crearCampo("autoridad_emisora", "text", true, "FDA, EMA, etc."),
                        crearCampo("numero_registro", "text", true, "Número de registro"),
                        crearCampo("fecha_emision", "date", true, "Fecha de emisión"),
                        crearCampo("archivo", "file", true, "Certificado PDF")
                ))
                .build());

        docs.add(DocumentoRequeridoDTO.builder()
                .id("autorizacion_fabricante")
                .nombre("Autorización del Fabricante al Importador")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Carta de autorización del fabricante extranjero")
                .categoria(CategoriaDocumento.OTROS)
                .obligatorio(true)
                .orden(31)
                .icono("handshake")
                .campos(Arrays.asList(
                        crearCampo("fabricante", "text", true, "Nombre del fabricante"),
                        crearCampo("importador", "text", true, "Nombre del importador"),
                        crearCampo("productos_autorizados", "textarea", true, "Lista de productos"),
                        crearCampo("fecha_autorizacion", "date", true, "Fecha de autorización"),
                        crearCampo("archivo", "file", true, "Carta PDF")
                ))
                .build());

        docs.add(DocumentoRequeridoDTO.builder()
                .id("traducciones_oficiales")
                .nombre("Traducciones Oficiales")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Traducciones oficiales de documentos en idioma extranjero")
                .categoria(CategoriaDocumento.OTROS)
                .obligatorio(true)
                .orden(32)
                .icono("language")
                .campos(Arrays.asList(
                        crearCampo("documento_original", "text", true, "Nombre del documento traducido"),
                        crearCampo("idioma_original", "text", true, "Idioma original"),
                        crearCampo("traductor_oficial", "text", true, "Nombre del traductor oficial"),
                        crearCampo("fecha_traduccion", "date", true, "Fecha de traducción"),
                        crearCampo("archivo", "file", true, "Traducción PDF")
                ))
                .build());

        return docs;
    }

    /**
     * Documentos para renovación/modificación
     */
    private List<DocumentoRequeridoDTO> getDocumentosRenovacion() {
        List<DocumentoRequeridoDTO> docs = new ArrayList<>();

        docs.add(DocumentoRequeridoDTO.builder()
                .id("registro_anterior")
                .nombre("Registro Sanitario Anterior")
                .tipo(TipoDocumentoRequerido.EXTERNO)
                .formato(FormatoDocumento.PDF)
                .descripcion("Copia del registro sanitario vigente o anterior")
                .categoria(CategoriaDocumento.OTROS)
                .obligatorio(true)
                .orden(40)
                .icono("file-medical-alt")
                .campos(Arrays.asList(
                        crearCampo("numero_registro_anterior", "text", true, "Número de registro"),
                        crearCampo("fecha_expedicion", "date", true, "Fecha de expedición"),
                        crearCampo("archivo", "file", true, "Registro PDF")
                ))
                .build());

        return docs;
    }

    /**
     * Genera advertencias según el tipo de trámite y clasificación
     */
    private List<String> generarAdvertencias(TipoTramiteINVIMA tipoTramite, ClasificacionProductoDTO clasificacion) {
        List<String> advertencias = new ArrayList<>();

        switch (tipoTramite) {
            case RSA:
                advertencias.add("⚠️ Este trámite requiere estudios de estabilidad y certificación HACCP");
                advertencias.add("⏱️ El tiempo de evaluación puede extenderse de 60 a 90 días hábiles");
                advertencias.add("🔬 Se requieren análisis de laboratorio acreditado por el ONAC");
                advertencias.add("✅ El establecimiento debe contar con certificación BPM INVIMA vigente");

                if (Boolean.TRUE.equals(clasificacion.getEsImportado())) {
                    advertencias.add("🌍 Productos importados requieren documentación adicional del país de origen");
                    advertencias.add("📄 Todos los documentos en idioma extranjero deben traducirse oficialmente");
                }

                if (esPoblacionVulnerable(clasificacion.getPoblacionObjetivo())) {
                    advertencias.add("👶 Productos para población vulnerable requieren advertencias especiales en el etiquetado");
                    advertencias.add("🔬 Se requieren estudios nutricionales especializados");
                }

                if (esProcesamientoAltoRiesgo(clasificacion.getProcesamiento())) {
                    advertencias.add("🌡️ El tipo de procesamiento requiere controles adicionales de temperatura y almacenamiento");
                }

                if (esCategoriaRiesgoAlto(clasificacion.getCategoria())) {
                    advertencias.add("🥩 La categoría del producto tiene riesgo inherente alto y requiere controles estrictos");
                }
                break;

            case PSA:
                advertencias.add("🔬 Se requieren análisis de laboratorio acreditado por el ONAC");
                advertencias.add("✅ El establecimiento debe contar con certificación BPM vigente");

                if (Boolean.TRUE.equals(clasificacion.getEsImportado())) {
                    advertencias.add("🌍 Productos importados requieren documentación adicional del país de origen");
                }

                if (esCategoriaRiesgoAlto(clasificacion.getCategoria())) {
                    advertencias.add("⚠️ La categoría del producto requiere controles adicionales de calidad");
                }
                break;

            case NSO:
                advertencias.add("📋 Trámite de bajo riesgo - proceso simplificado");
                advertencias.add("⏱️ Tiempo estimado: 15-30 días hábiles");
                advertencias.add("✅ Verificar que todos los documentos estén actualizados y vigentes");
                break;
        }

        return advertencias;
    }

    /**
     * Obtiene la descripción del tipo de trámite
     */
    private String obtenerDescripcionTramite(TipoTramiteINVIMA tipoTramite) {
        return switch (tipoTramite) {
            case NSO ->
                    "Notificación Sanitaria Obligatoria - Para productos de bajo riesgo y población general";
            case PSA ->
                    "Permiso Sanitario - Para productos de riesgo medio que requieren control físico-químico y microbiológico";
            case RSA ->
                    "Registro Sanitario - Para productos de alto riesgo o población vulnerable que requieren HACCP y estudios de estabilidad";
        };
    }

    /**
     * Obtiene el tiempo estimado según el tipo de trámite
     */
    private String obtenerTiempoEstimado(TipoTramiteINVIMA tipoTramite) {
        return switch (tipoTramite) {
            case NSO -> "15-30 días hábiles";
            case PSA -> "30-60 días hábiles";
            case RSA -> "60-90 días hábiles";
        };
    }

    /**
     * Obtiene el costo estimado según el tipo de trámite
     */
    private String obtenerCostoEstimado(TipoTramiteINVIMA tipoTramite) {
        return switch (tipoTramite) {
            case NSO -> "1-2 SMMLV";
            case PSA -> "3-5 SMMLV";
            case RSA -> "8-15 SMMLV";
        };
    }

    /**
     * Verifica si la población objetivo es vulnerable
     */
    private boolean esPoblacionVulnerable(String poblacion) {
        if (poblacion == null) return false;
        String poblacionLower = poblacion.toLowerCase();
        return POBLACIONES_VULNERABLES.stream()
                .anyMatch(poblacionLower::contains);
    }

    /**
     * Verifica si el procesamiento es de alto riesgo
     */
    private boolean esProcesamientoAltoRiesgo(String procesamiento) {
        if (procesamiento == null) return false;
        String procesamientoLower = procesamiento.toLowerCase();
        return PROCESAMIENTOS_ALTO_RIESGO.stream()
                .anyMatch(procesamientoLower::contains);
    }

    /**
     * Verifica si la categoría tiene riesgo inherente alto
     */
    private boolean esCategoriaRiesgoAlto(String categoria) {
        if (categoria == null) return false;
        String categoriaLower = categoria.toLowerCase();
        return CATEGORIAS_RIESGO_ALTO.stream()
                .anyMatch(categoriaLower::contains);
    }

    /**
     * Método auxiliar para crear campos
     */
    private CampoDocumentoDTO crearCampo(String nombre, String tipo, Boolean requerido, String placeholder) {
        return CampoDocumentoDTO.builder()
                .nombre(nombre)
                .tipo(tipo)
                .requerido(requerido)
                .placeholder(placeholder)
                .build();
    }
}

