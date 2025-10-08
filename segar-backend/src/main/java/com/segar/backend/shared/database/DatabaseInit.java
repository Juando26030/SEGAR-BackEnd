package com.segar.backend.shared.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.segar.backend.shared.domain.*;
import com.segar.backend.tramites.domain.*;
import com.segar.backend.tramites.infrastructure.*;
import com.segar.backend.shared.infrastructure.*;
import com.segar.backend.documentos.domain.*;
import com.segar.backend.documentos.infrastructure.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import jakarta.transaction.Transactional;

@Controller
@Transactional
@Profile("default")
public class DatabaseInit implements ApplicationRunner{

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TramiteRepository tramiteRepo;

    @Autowired
    private EventoTramiteRepository eventoRepo;

    @Autowired
    private RequerimientoRepository reqRepo;

    @Autowired
    private NotificacionRepository notifRepo;

    @Autowired
    private PreferenciasNotificacionRepository prefRepo;

    @Autowired
    private ResolucionRepository resolucionRepository;

    @Autowired
    private RegistroSanitarioRepository registroSanitarioRepository;

    @Autowired
    private HistorialTramiteRepository historialTramiteRepository;

    // Nuevos repositorios para documentos dinámicos y solicitudes
    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PagoRepository pagoRepository;

    // Repositorio para documentos de trámites INVIMA
    @Autowired
    private TramiteDocumentoRepository tramiteDocumentoRepository;

    @Override
    public void run(ApplicationArguments args) {
        // ========== INICIALIZAR PLANTILLAS DE DOCUMENTOS DINÁMICOS ==========
        initializeDocumentTemplates();

        // ========== DATOS EXISTENTES DE PRODUCTOS Y TRÁMITES ==========
        productoRepository.save(new Producto("Producto 1", "Descripción del producto 1", "Especificaciones del producto 1", "Referencia del producto 1", "Fabricante del producto 1"));
        Producto yogurt = productoRepository.save(new Producto("Yogurt Natural", "Yogurt natural sin azúcar añadida", "Contenido graso 3.5%, proteína 4g por 100ml", "YOG-001", "Lácteos del Valle S.A.S."));
        Producto mermelada = productoRepository.save(new Producto("Mermelada de Fresa", "Mermelada artesanal de fresa", "Sin conservantes artificiales, 65% fruta", "MER-002", "Dulces Tradicionales Ltda."));
        Producto aceite = productoRepository.save(new Producto("Aceite de Oliva Extra Virgen", "Aceite de oliva primera extracción en frío", "Acidez máxima 0.3%, origen español", "ACE-003", "Gourmet Foods Colombia S.A.S."));

        // ========== PAGOS ==========
        Pago pago1 = Pago.builder()
                .empresaId(1001L)
                .monto(new BigDecimal("1250000.00"))
                .metodoPago(MetodoPago.TARJETA_CREDITO)
                .estado(EstadoPago.APROBADO)
                .referenciaPago("PAY-2024-001")
                .fechaPago(LocalDateTime.of(2024, 8, 20, 10, 30))
                .concepto("Pago tarifa registro sanitario - Yogurt Natural")
                .build();
        pagoRepository.save(pago1);

        Pago pago2 = Pago.builder()
                .empresaId(1002L)
                .monto(new BigDecimal("890000.00"))
                .metodoPago(MetodoPago.PSE)
                .estado(EstadoPago.APROBADO)
                .referenciaPago("PAY-2024-002")
                .fechaPago(LocalDateTime.of(2024, 8, 21, 14, 15))
                .concepto("Pago tarifa registro sanitario - Mermelada de Fresa")
                .build();
        pagoRepository.save(pago2);

        Pago pago3 = Pago.builder()
                .empresaId(1003L)
                .monto(new BigDecimal("1150000.00"))
                .metodoPago(MetodoPago.TRANSFERENCIA)
                .estado(EstadoPago.APROBADO)
                .referenciaPago("PAY-2024-003")
                .fechaPago(LocalDateTime.of(2024, 8, 22, 9, 45))
                .concepto("Pago tarifa registro sanitario - Aceite de Oliva")
                .build();
        pagoRepository.save(pago3);

        // ========== SOLICITUDES ==========
        Solicitud solicitud1 = Solicitud.builder()
                .empresaId(1001L)
                .producto(yogurt)
                .tipoTramite(TipoTramite.REGISTRO)
                .estado(EstadoSolicitud.RADICADA)
                .numeroRadicado("INV-20240820-000001")
                .fechaRadicacion(LocalDateTime.of(2024, 8, 20, 11, 30))
                .observaciones("Solicitud de registro sanitario para yogurt natural - YA RADICADA")
                .pago(pago1)
                .build();
        solicitudRepository.save(solicitud1);

        Solicitud solicitud2 = Solicitud.builder()
                .empresaId(1002L)
                .producto(mermelada)
                .tipoTramite(TipoTramite.REGISTRO)
                .estado(EstadoSolicitud.BORRADOR)
                .observaciones("Solicitud lista para radicar - documentos y pago completos")
                .pago(pago2)
                .build();
        solicitudRepository.save(solicitud2);

        Solicitud solicitud3 = Solicitud.builder()
                .empresaId(1003L)
                .producto(aceite)
                .tipoTramite(TipoTramite.REGISTRO)
                .estado(EstadoSolicitud.BORRADOR)
                .observaciones("Solicitud con documentos faltantes")
                .pago(pago3)
                .build();
        solicitudRepository.save(solicitud3);

        // ========== DOCUMENTOS PARA SOLICITUDES ==========
        // Documentos para solicitud 1 (completos)
        documentoRepository.save(Documento.builder()
                .nombreArchivo("certificado_constitucion_lacteos_valle.pdf")
                .tipoDocumento(TipoDocumento.CERTIFICADO_CONSTITUCION)
                .rutaArchivo("/uploads/docs/cert_const_001.pdf")
                .tamanioArchivo(2048576L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 20, 9, 0))
                .solicitud(solicitud1)
                .obligatorio(true)
                .archivo("documento_simulado_certificado")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("rut_lacteos_valle.pdf")
                .tipoDocumento(TipoDocumento.RUT)
                .rutaArchivo("/uploads/docs/rut_001.pdf")
                .tamanioArchivo(1024000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 20, 9, 15))
                .solicitud(solicitud1)
                .obligatorio(true)
                .archivo("documento_simulado_rut")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("concepto_sanitario_planta.pdf")
                .tipoDocumento(TipoDocumento.CONCEPTO_SANITARIO)
                .rutaArchivo("/uploads/docs/concepto_san_001.pdf")
                .tamanioArchivo(3072000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 20, 9, 30))
                .solicitud(solicitud1)
                .obligatorio(true)
                .archivo("documento_simulado_concepto")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("ficha_tecnica_yogurt.pdf")
                .tipoDocumento(TipoDocumento.FICHA_TECNICA)
                .rutaArchivo("/uploads/docs/ficha_tec_001.pdf")
                .tamanioArchivo(1536000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 20, 9, 45))
                .solicitud(solicitud1)
                .obligatorio(true)
                .archivo("documento_simulado_ficha")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("etiqueta_yogurt_natural.pdf")
                .tipoDocumento(TipoDocumento.ETIQUETA)
                .rutaArchivo("/uploads/docs/etiqueta_001.pdf")
                .tamanioArchivo(2048000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 20, 10, 0))
                .solicitud(solicitud1)
                .obligatorio(true)
                .archivo("documento_simulado_etiqueta")
                .build());

        // Documentos para solicitud 2 (completos)
        documentoRepository.save(Documento.builder()
                .nombreArchivo("certificado_constitucion_dulces.pdf")
                .tipoDocumento(TipoDocumento.CERTIFICADO_CONSTITUCION)
                .rutaArchivo("/uploads/docs/cert_const_002.pdf")
                .tamanioArchivo(1856000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 21, 10, 0))
                .solicitud(solicitud2)
                .obligatorio(true)
                .archivo("documento_simulado_certificado_2")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("rut_dulces_tradicionales.pdf")
                .tipoDocumento(TipoDocumento.RUT)
                .rutaArchivo("/uploads/docs/rut_002.pdf")
                .tamanioArchivo(768000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 21, 10, 15))
                .solicitud(solicitud2)
                .obligatorio(true)
                .archivo("documento_simulado_rut_2")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("concepto_sanitario_dulces.pdf")
                .tipoDocumento(TipoDocumento.CONCEPTO_SANITARIO)
                .rutaArchivo("/uploads/docs/concepto_san_002.pdf")
                .tamanioArchivo(2304000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 21, 10, 30))
                .solicitud(solicitud2)
                .obligatorio(true)
                .archivo("documento_simulado_concepto_2")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("ficha_tecnica_mermelada.pdf")
                .tipoDocumento(TipoDocumento.FICHA_TECNICA)
                .rutaArchivo("/uploads/docs/ficha_tec_002.pdf")
                .tamanioArchivo(1280000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 21, 10, 45))
                .solicitud(solicitud2)
                .obligatorio(true)
                .archivo("documento_simulado_ficha_2")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("etiqueta_mermelada_fresa.pdf")
                .tipoDocumento(TipoDocumento.ETIQUETA)
                .rutaArchivo("/uploads/docs/etiqueta_002.pdf")
                .tamanioArchivo(1920000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 21, 11, 0))
                .solicitud(solicitud2)
                .obligatorio(true)
                .archivo("documento_simulado_etiqueta_2")
                .build());

        // Documentos para solicitud 3 (incompletos - solo 2)
        documentoRepository.save(Documento.builder()
                .nombreArchivo("certificado_constitucion_gourmet.pdf")
                .tipoDocumento(TipoDocumento.CERTIFICADO_CONSTITUCION)
                .rutaArchivo("/uploads/docs/cert_const_003.pdf")
                .tamanioArchivo(2100000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 22, 8, 0))
                .solicitud(solicitud3)
                .obligatorio(true)
                .archivo("documento_simulado_certificado_3")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("rut_gourmet_foods.pdf")
                .tipoDocumento(TipoDocumento.RUT)
                .rutaArchivo("/uploads/docs/rut_003.pdf")
                .tamanioArchivo(850000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 22, 8, 15))
                .solicitud(solicitud3)
                .obligatorio(true)
                .archivo("documento_simulado_rut_3")
                .build());

        // Documentos disponibles sin asignar a solicitud (para testing de disponibilidad)
        documentoRepository.save(Documento.builder()
                .nombreArchivo("documento_disponible_1.pdf")
                .tipoDocumento(TipoDocumento.CERTIFICADO_BPM)
                .rutaArchivo("/uploads/docs/disponible_001.pdf")
                .tamanioArchivo(1500000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 25, 10, 0))
                .solicitud(null)
                .obligatorio(false)
                .archivo("documento_disponible_simulado_1")
                .build());

        documentoRepository.save(Documento.builder()
                .nombreArchivo("documento_disponible_2.pdf")
                .tipoDocumento(TipoDocumento.ANALISIS_MICROBIOLOGICO)
                .rutaArchivo("/uploads/docs/disponible_002.pdf")
                .tamanioArchivo(1800000L)
                .tipoMime("application/pdf")
                .fechaCarga(LocalDateTime.of(2024, 8, 25, 10, 30))
                .solicitud(null)
                .obligatorio(false)
                .archivo("documento_disponible_simulado_2")
                .build());

        // ========== TRÁMITE BASE ==========
        Tramite t = new Tramite();
        t.setRadicadoNumber("2024-001234-56789");
        t.setSubmissionDate(LocalDate.of(2024,3,15));
        t.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t.setProductName("Yogurt Natural Premium ");
        t.setCurrentStatus(EstadoTramite.EN_EVALUACION_TECNICA);
        t.setLastUpdate(LocalDateTime.now());
        tramiteRepo.save(t);

        // Eventos timeline usando constructores normales
        EventoTramite e1 = new EventoTramite();
        e1.setTramite(t);
        e1.setTitle("Solicitud Radicada");
        e1.setDescription("Documentos recibidos y radicado asignado");
        e1.setDate(LocalDate.of(2024,3,15));
        e1.setCompleted(true);
        e1.setCurrentEvent(false);
        e1.setOrden(1);

        EventoTramite e2 = new EventoTramite();
        e2.setTramite(t);
        e2.setTitle("Verificación Documental");
        e2.setDescription("Revisión inicial de documentos completada");
        e2.setDate(LocalDate.of(2024,3,20));
        e2.setCompleted(true);
        e2.setCurrentEvent(false);
        e2.setOrden(2);

        EventoTramite e3 = new EventoTramite();
        e3.setTramite(t);
        e3.setTitle("Evaluación Técnica");
        e3.setDescription("Análisis técnico del producto en curso");
        e3.setDate(LocalDate.of(2024,3,25));
        e3.setCompleted(false);
        e3.setCurrentEvent(true);
        e3.setOrden(3);

        eventoRepo.saveAll(List.of(e1,e2,e3));

        // Requerimientos usando constructores normales
        Requerimiento r1 = new Requerimiento();
        r1.setTramite(t);
        r1.setNumber("REQ-2024-001234-01");
        r1.setTitle("Información nutricional complementaria");
        r1.setDescription("Se requiere información adicional sobre los valores nutricionales del producto");
        r1.setDate(LocalDate.of(2024,4,2));
        r1.setDeadline(LocalDate.now().plusDays(12));
        r1.setStatus(EstadoRequerimiento.PENDIENTE);

        Requerimiento r2 = new Requerimiento();
        r2.setTramite(t);
        r2.setNumber("REQ-2024-001234-02");
        r2.setTitle("Certificaciones de calidad");
        r2.setDescription("Presentar certificaciones ISO y BPM del fabricante");
        r2.setDate(LocalDate.of(2024,4,5));
        r2.setDeadline(LocalDate.now().plusDays(10));
        r2.setStatus(EstadoRequerimiento.PENDIENTE);

        Requerimiento r3 = new Requerimiento();
        r3.setTramite(t);
        r3.setNumber("REQ-2024-001234-03");
        r3.setTitle("Análisis microbiológicos");
        r3.setDescription("Resultados de análisis microbiológicos actualizados");
        r3.setDate(LocalDate.of(2024,4,8));
        r3.setDeadline(LocalDate.now().plusDays(15));
        r3.setStatus(EstadoRequerimiento.RESPONDIDO);

        reqRepo.saveAll(List.of(r1,r2,r3));

        // Notificaciones usando constructores normales
        Notificacion n1 = new Notificacion();
        n1.setTramite(t);
        n1.setType(TipoNotificacion.INFO);
        n1.setTitle("Nuevo requerimiento generado");
        n1.setMessage("Se ha generado un nuevo requerimiento para su trámite");
        n1.setDate(LocalDateTime.now().minusDays(2));
        n1.setRead(false);

        Notificacion n2 = new Notificacion();
        n2.setTramite(t);
        n2.setType(TipoNotificacion.INFO);
        n2.setTitle("Actualización de estado");
        n2.setMessage("Su trámite ha avanzado a la etapa de evaluación técnica");
        n2.setDate(LocalDateTime.now().minusDays(5));
        n2.setRead(true);

        notifRepo.saveAll(List.of(n1,n2));

        // Preferencias de notificación usando constructor normal
        PreferenciasNotificacion pref = new PreferenciasNotificacion();
        pref.setTramite(t);
        pref.setEmail(true);
        pref.setSms(false);
        pref.setRequirements(true);
        pref.setStatusUpdates(true);
        prefRepo.save(pref);

        // **DATOS PARA MÓDULO DE RESOLUCIÓN - NUEVOS**

        // 1. Crear resolución de ejemplo
        Resolucion resolucion = Resolucion.builder()
                .numeroResolucion("2024-INVIMA-0001")
                .fechaEmision(LocalDateTime.of(2024, 8, 15, 10, 30))
                .autoridad("INVIMA - Instituto Nacional de Vigilancia de Medicamentos y Alimentos")
                .estado(EstadoResolucion.APROBADA)
                .observaciones("Solicitud aprobada. El producto 'Yogurt Natural Premium' cumple con todos los requisitos técnicos y normativos establecidos para alimentos de riesgo medio. Se autoriza su comercialización en el territorio nacional.")
                .tramiteId(t.getId())
                .documentoUrl("/documents/resolucion-2024-invima-0001.pdf")
                .fechaNotificacion(LocalDateTime.of(2024, 8, 15, 14, 0))
                .build();

        // Guardar resolución usando el repositorio que ya existe
        resolucionRepository.save(resolucion);

        // 2. Crear registro sanitario
        RegistroSanitario registro = RegistroSanitario.builder()
                .numeroRegistro("RSAA21M-20240001")
                .fechaExpedicion(LocalDateTime.of(2024, 8, 15, 15, 0))
                .fechaVencimiento(LocalDateTime.of(2029, 8, 15, 23, 59)) // 5 años de vigencia
                .productoId(1L)
                .empresaId(1L)
                .estado(EstadoRegistro.VIGENTE)
                .resolucionId(resolucion.getId())
                .documentoUrl("/documents/registro-sanitario-rsaa21m-20240001.pdf")
                .build();

        registroSanitarioRepository.save(registro);

        // 3. Crear historial del trámite
        HistorialTramite h1 = HistorialTramite.builder()
                .tramiteId(t.getId())
                .fecha(LocalDateTime.of(2024, 3, 15, 9, 0))
                .accion("TRAMITE_RADICADO")
                .descripcion("Trámite radicado exitosamente. Documentos recibidos y verificación inicial completada.")
                .usuario("Sistema SEGAR")
                .estado("RADICADO")
                .build();

        HistorialTramite h2 = HistorialTramite.builder()
                .tramiteId(t.getId())
                .fecha(LocalDateTime.of(2024, 3, 20, 14, 30))
                .accion("VERIFICACION_DOCUMENTAL")
                .descripcion("Verificación documental completada. Todos los documentos están completos y en regla.")
                .usuario("Técnico INVIMA - María González")
                .estado("VERIFICADO")
                .build();

        HistorialTramite h3 = HistorialTramite.builder()
                .tramiteId(t.getId())
                .fecha(LocalDateTime.of(2024, 3, 25, 11, 0))
                .accion("EVALUACION_TECNICA_INICIADA")
                .descripcion("Inicio de evaluación técnica del producto. Análisis de composición y especificaciones.")
                .usuario("Especialista INVIMA - Dr. Carlos Mendoza")
                .estado("EN_EVALUACION")
                .build();

        HistorialTramite h4 = HistorialTramite.builder()
                .tramiteId(t.getId())
                .fecha(LocalDateTime.of(2024, 8, 15, 10, 30))
                .accion("RESOLUCION_EMITIDA")
                .descripcion("Resolución de aprobación emitida. Registro sanitario autorizado para comercialización.")
                .usuario("Director Técnico INVIMA - Dra. Ana Patricia Ruiz")
                .estado("APROBADO")
                .build();

        historialTramiteRepository.saveAll(List.of(h1, h2, h3, h4));

        System.out.println("✅ Datos de inicialización cargados correctamente");
        System.out.println("📋 Trámite ID: " + t.getId());
        System.out.println("📄 Resolución: " + resolucion.getNumeroResolucion());
        System.out.println("🏥 Registro Sanitario: " + registro.getNumeroRegistro());
    }

    /**
     * Inicializa las plantillas de documentos dinámicos basadas en requisitos INVIMA
     */
    private void initializeDocumentTemplates() {
        System.out.println("📄 Inicializando plantillas de documentos dinámicos...");

        // 1. Formulario de Solicitud
        createDocumentTemplate(
                "FORMULARIO_SOLICITUD",
                "Formulario de Solicitud de Registro Sanitario",
                "Formulario oficial de solicitud de registro sanitario según formato ASS-RSA-FM099 de INVIMA",
                "[{\"key\":\"numero_solicitud\",\"label\":\"Número de solicitud\",\"type\":\"text\",\"required\":true,\"maxLength\":50}," +
                "{\"key\":\"tipo_tramite\",\"label\":\"Tipo de trámite\",\"type\":\"select\",\"required\":true,\"options\":[\"REGISTRO\",\"RENOVACION\",\"MODIFICACION\"]}," +
                "{\"key\":\"solicitante_nombre\",\"label\":\"Nombre del solicitante\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"solicitante_nit\",\"label\":\"NIT del solicitante\",\"type\":\"text\",\"required\":true,\"maxLength\":20}," +
                "{\"key\":\"solicitante_direccion\",\"label\":\"Dirección del solicitante\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"solicitante_telefono\",\"label\":\"Teléfono\",\"type\":\"tel\",\"required\":true}," +
                "{\"key\":\"solicitante_email\",\"label\":\"Correo electrónico\",\"type\":\"email\",\"required\":true}," +
                "{\"key\":\"representante_legal\",\"label\":\"Representante legal\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"apoderado\",\"label\":\"Apoderado (si aplica)\",\"type\":\"text\",\"required\":false,\"maxLength\":200}," +
                "{\"key\":\"producto_nombre\",\"label\":\"Nombre del producto\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"categoria_riesgo\",\"label\":\"Categoría de riesgo\",\"type\":\"select\",\"required\":true,\"options\":[\"ALTO\",\"MEDIO\",\"BAJO\"]}," +
                "{\"key\":\"tipo_producto\",\"label\":\"Tipo de producto\",\"type\":\"select\",\"required\":true,\"options\":[\"ALIMENTO\",\"BEBIDA\",\"SUPLEMENTO\",\"OTROS\"]}," +
                "{\"key\":\"uso_previsto\",\"label\":\"Uso previsto\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"pais_origen\",\"label\":\"País de origen\",\"type\":\"text\",\"required\":true,\"maxLength\":100}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":10485760,\"multipleAllowed\":false}",
                1, true, true, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.RENOVACION, TipoTramite.MODIFICACION)
        );

        // 2. Ficha Técnica
        createDocumentTemplate(
                "FICHA_TECNICA",
                "Ficha Técnica del Producto",
                "Ficha técnica detallada del producto según requisitos INVIMA para alimentos",
                "[{\"key\":\"nombre_comercial\",\"label\":\"Nombre comercial\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"nombre_tecnico\",\"label\":\"Nombre técnico/INCI\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"descripcion_producto\",\"label\":\"Descripción del producto\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"composicion_cualitativa\",\"label\":\"Composición cualitativa\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"composicion_cuantitativa\",\"label\":\"Composición cuantitativa\",\"type\":\"table\",\"required\":true,\"columns\":[\"ingrediente\",\"porcentaje\",\"unidad\"]}," +
                "{\"key\":\"proceso_fabricacion\",\"label\":\"Proceso de fabricación\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"vida_util\",\"label\":\"Vida útil declarada (días)\",\"type\":\"number\",\"required\":true,\"min\":1}," +
                "{\"key\":\"condiciones_almacenamiento\",\"label\":\"Condiciones de almacenamiento\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"presentacion_comercial\",\"label\":\"Presentación comercial\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"especificaciones_fisicoquimicas\",\"label\":\"Especificaciones fisicoquímicas\",\"type\":\"table\",\"required\":true,\"columns\":[\"parametro\",\"unidad\",\"limite_min\",\"limite_max\"]}," +
                "{\"key\":\"especificaciones_microbiologicas\",\"label\":\"Especificaciones microbiológicas\",\"type\":\"table\",\"required\":true,\"columns\":[\"microorganismo\",\"limite\",\"unidad\",\"metodo\"]}," +
                "{\"key\":\"pais_origen\",\"label\":\"País de origen\",\"type\":\"text\",\"required\":true,\"maxLength\":100}]",
                "{\"allowedMime\":[\"application/pdf\",\"image/jpeg\",\"image/png\"],\"maxSize\":15728640,\"multipleAllowed\":true}",
                2, true, true, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.MODIFICACION)
        );

        // 3. Etiqueta
        createDocumentTemplate(
                "ETIQUETA",
                "Etiqueta y Rotulado",
                "Arte final de etiqueta con información nutricional y advertencias según normativa INVIMA",
                "[{\"key\":\"nombre_comercial\",\"label\":\"Nombre comercial\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"marca\",\"label\":\"Marca\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"informacion_nutricional\",\"label\":\"Información nutricional\",\"type\":\"table\",\"required\":true,\"columns\":[\"nutriente\",\"cantidad\",\"porcentaje_vd\"]}," +
                "{\"key\":\"declaraciones_nutricionales\",\"label\":\"Declaraciones nutricionales\",\"type\":\"textarea\",\"required\":false}," +
                "{\"key\":\"declaraciones_saludables\",\"label\":\"Declaraciones de propiedades saludables\",\"type\":\"textarea\",\"required\":false}," +
                "{\"key\":\"advertencias\",\"label\":\"Advertencias obligatorias\",\"type\":\"textarea\",\"required\":false}," +
                "{\"key\":\"lote_info\",\"label\":\"Información de lote\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"fecha_vencimiento_formato\",\"label\":\"Formato fecha de vencimiento\",\"type\":\"text\",\"required\":true,\"maxLength\":50}," +
                "{\"key\":\"fabricante_nombre\",\"label\":\"Nombre del fabricante\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"fabricante_direccion\",\"label\":\"Dirección del fabricante\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"importador_info\",\"label\":\"Información del importador (si aplica)\",\"type\":\"text\",\"required\":false,\"maxLength\":300}," +
                "{\"key\":\"arte_final\",\"label\":\"Arte final de etiqueta\",\"type\":\"file\",\"required\":true}]",
                "{\"allowedMime\":[\"application/pdf\",\"image/jpeg\",\"image/png\",\"application/zip\"],\"maxSize\":20971520,\"multipleAllowed\":true}",
                3, true, true, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.MODIFICACION)
        );

        // 4. Certificado de Análisis
        createDocumentTemplate(
                "CERTIFICADO_ANALISIS",
                "Certificado de Análisis (COA)",
                "Certificado de análisis emitido por laboratorio acreditado",
                "[{\"key\":\"laboratorio_nombre\",\"label\":\"Nombre del laboratorio\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"laboratorio_acreditacion\",\"label\":\"Número de acreditación\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"muestra_lote\",\"label\":\"Lote analizado\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"fecha_muestreo\",\"label\":\"Fecha de muestreo\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"fecha_analisis\",\"label\":\"Fecha de análisis\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"parametros_evaluados\",\"label\":\"Parámetros evaluados\",\"type\":\"table\",\"required\":true,\"columns\":[\"parametro\",\"unidad\",\"limite\",\"resultado\",\"cumple\"]}," +
                "{\"key\":\"metodos_analisis\",\"label\":\"Métodos de análisis utilizados\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"responsable_tecnico\",\"label\":\"Responsable técnico\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"observaciones\",\"label\":\"Observaciones\",\"type\":\"textarea\",\"required\":false}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":10485760,\"multipleAllowed\":false}",
                4, true, true, CategoriaRiesgo.III,
                Set.of(TipoTramite.REGISTRO)
        );

        // 5. Certificado BPM
        createDocumentTemplate(
                "CERTIFICADO_BPM",
                "Certificado de Buenas Prácticas de Manufactura",
                "Certificado BPM de la planta de producción emitido por autoridad competente",
                "[{\"key\":\"numero_certificado\",\"label\":\"Número del certificado\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"entidad_emisora\",\"label\":\"Entidad emisora\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"planta_nombre\",\"label\":\"Nombre de la planta\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"planta_direccion\",\"label\":\"Dirección de la planta\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"fecha_expedicion\",\"label\":\"Fecha de expedición\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"fecha_vencimiento\",\"label\":\"Fecha de vencimiento\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"alcance_certificacion\",\"label\":\"Alcance de la certificación\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"productos_certificados\",\"label\":\"Productos certificados\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"observaciones\",\"label\":\"Observaciones\",\"type\":\"textarea\",\"required\":false}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":10485760,\"multipleAllowed\":false}",
                5, true, true, null,
                Set.of(TipoTramite.REGISTRO)
        );

        // 6. Certificado de Existencia
        createDocumentTemplate(
                "CERTIFICADO_EXISTENCIA",
                "Certificado de Existencia y Representación Legal",
                "Certificado de existencia y representación legal o matrícula mercantil vigente",
                "[{\"key\":\"numero_certificado\",\"label\":\"Número del certificado\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"camara_comercio\",\"label\":\"Cámara de comercio emisora\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"fecha_expedicion\",\"label\":\"Fecha de expedición\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"vigencia\",\"label\":\"Vigencia (días)\",\"type\":\"number\",\"required\":true,\"min\":1}," +
                "{\"key\":\"razon_social\",\"label\":\"Razón social\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"nit\",\"label\":\"NIT\",\"type\":\"text\",\"required\":true,\"maxLength\":20}," +
                "{\"key\":\"representante_legal\",\"label\":\"Representante legal\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"objeto_social\",\"label\":\"Objeto social\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"capital_autorizado\",\"label\":\"Capital autorizado\",\"type\":\"number\",\"required\":false}," +
                "{\"key\":\"capital_suscrito\",\"label\":\"Capital suscrito\",\"type\":\"number\",\"required\":false}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":5242880,\"multipleAllowed\":false}",
                6, true, true, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.RENOVACION, TipoTramite.MODIFICACION)
        );

        // 7. Poder de Representación
        createDocumentTemplate(
                "PODER_REPRESENTACION",
                "Poder o Carta de Representación",
                "Poder otorgado al apoderado para realizar el trámite ante INVIMA",
                "[{\"key\":\"poderdante_nombre\",\"label\":\"Nombre del poderdante\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"poderdante_identificacion\",\"label\":\"Identificación del poderdante\",\"type\":\"text\",\"required\":true,\"maxLength\":50}," +
                "{\"key\":\"apoderado_nombre\",\"label\":\"Nombre del apoderado\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"apoderado_identificacion\",\"label\":\"Identificación del apoderado\",\"type\":\"text\",\"required\":true,\"maxLength\":50}," +
                "{\"key\":\"objeto_poder\",\"label\":\"Objeto del poder\",\"type\":\"textarea\",\"required\":true}," +
                "{\"key\":\"fecha_expedicion\",\"label\":\"Fecha de expedición\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"notaria\",\"label\":\"Notaría donde se otorgó\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"numero_escritura\",\"label\":\"Número de escritura\",\"type\":\"text\",\"required\":false,\"maxLength\":100}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":5242880,\"multipleAllowed\":false}",
                7, true, false, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.RENOVACION, TipoTramite.MODIFICACION)
        );

        // 8. Comprobante de Pago
        createDocumentTemplate(
                "COMPROBANTE_PAGO",
                "Comprobante de Pago de Tasas INVIMA",
                "Recibo de pago de tasas oficial de INVIMA para el trámite",
                "[{\"key\":\"numero_recibo\",\"label\":\"Número de recibo\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"entidad_financiera\",\"label\":\"Entidad financiera\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"referencia_pago\",\"label\":\"Referencia de pago\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"valor_pagado\",\"label\":\"Valor pagado\",\"type\":\"number\",\"required\":true,\"min\":0}," +
                "{\"key\":\"fecha_pago\",\"label\":\"Fecha de pago\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"tramite_asociado\",\"label\":\"Trámite asociado\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"concepto_pago\",\"label\":\"Concepto del pago\",\"type\":\"text\",\"required\":true,\"maxLength\":300}]",
                "{\"allowedMime\":[\"application/pdf\",\"image/jpeg\",\"image/png\"],\"maxSize\":5242880,\"multipleAllowed\":false}",
                8, true, true, null,
                Set.of(TipoTramite.REGISTRO, TipoTramite.RENOVACION, TipoTramite.MODIFICACION)
        );

        // 9. Certificado de Venta Libre
        createDocumentTemplate(
                "CERTIFICADO_VENTA_LIBRE",
                "Certificado de Venta Libre",
                "Certificado de venta libre emitido por autoridad competente del país de origen (para productos importados)",
                "[{\"key\":\"pais_emisor\",\"label\":\"País emisor\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"autoridad_competente\",\"label\":\"Autoridad competente emisora\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"numero_certificado\",\"label\":\"Número de certificado\",\"type\":\"text\",\"required\":true,\"maxLength\":100}," +
                "{\"key\":\"fecha_emision\",\"label\":\"Fecha de emisión\",\"type\":\"date\",\"required\":true}," +
                "{\"key\":\"vigencia\",\"label\":\"Vigencia (meses)\",\"type\":\"number\",\"required\":true,\"min\":1}," +
                "{\"key\":\"producto_certificado\",\"label\":\"Producto certificado\",\"type\":\"text\",\"required\":true,\"maxLength\":300}," +
                "{\"key\":\"fabricante\",\"label\":\"Fabricante\",\"type\":\"text\",\"required\":true,\"maxLength\":200}," +
                "{\"key\":\"observaciones\",\"label\":\"Observaciones\",\"type\":\"textarea\",\"required\":false}]",
                "{\"allowedMime\":[\"application/pdf\"],\"maxSize\":5242880,\"multipleAllowed\":false}",
                9, true, false, null,
                Set.of(TipoTramite.REGISTRO)
        );

        System.out.println("✅ Plantillas de documentos dinámicos inicializadas correctamente");
    }

    /**
     * Helper para crear plantillas de documentos
     */
    private void createDocumentTemplate(String code, String name, String description,
                                       String fieldsDefinition, String fileRules,
                                       int displayOrder, boolean active, boolean required,
                                       CategoriaRiesgo categoriaRiesgo,
                                       Set<TipoTramite> tramiteTypes) {
        DocumentTemplate template = DocumentTemplate.builder()
                .code(code)
                .name(name)
                .description(description)
                .fieldsDefinition(fieldsDefinition)
                .fileRules(fileRules)
                .version(1)
                .active(active)
                .required(required)
                .displayOrder(displayOrder)
                .categoriaRiesgo(categoriaRiesgo)
                .appliesToTramiteTypes(tramiteTypes)
                .createdBy("SYSTEM")
                .build();

        documentTemplateRepository.save(template);
    }
}
