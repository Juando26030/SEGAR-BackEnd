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
import com.segar.backend.calendario.infrastructure.EventoRepository;
import com.segar.backend.calendario.domain.*;

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

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EventoRepository eventoCalendarioRepository;

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
    public void run(ApplicationArguments args) throws Exception {

        // Crear empresa
        Empresa empresa = Empresa.builder()
                .razonSocial("Lácteos Premium S.A.S.")
                .nit("900123456-7")
                .nombreComercial("Premium Dairy")
                .direccion("Calle 123 #45-67")
                .ciudad("Bogotá")
                .pais("Colombia")
                .telefono("601-2345678")
                .email("contacto@premiumdairy.com")
                .representanteLegal("Juan Carlos Rodríguez")
                .estado(EstadoEmpresa.ACTIVA)
                .tipoEmpresa("FABRICANTE")
                .build();
        empresaRepository.save(empresa);

        // Crear productos
        Producto producto1 = new Producto("Yogurt Natural Premium", "Yogurt natural sin conservantes", "Contenido graso: 3.2%, Proteína: 4.5%", "YNP-2024-001", empresa.getRazonSocial(), empresa.getId());
        Producto producto2 = new Producto("Leche Deslactosada UHT", "Leche UHT sin lactosa", "Contenido graso: 1.5%, Sin lactosa", "LDU-2024-002", empresa.getRazonSocial(), empresa.getId());
        Producto producto3 = new Producto("Queso Mozzarella Premium", "Queso mozzarella artesanal", "Humedad: 60%, Grasa: 22%", "QMP-2024-003", empresa.getRazonSocial(), empresa.getId());
        Producto producto4 = new Producto("Mantequilla Sin Sal", "Mantequilla sin sal añadida", "Contenido graso: 82%", "MSS-2024-004", empresa.getRazonSocial(), empresa.getId());
        Producto producto5 = new Producto("Kumis Natural", "Kumis probiótico natural", "Probióticos: 10^8 UFC/ml", "KN-2024-005", empresa.getRazonSocial(), empresa.getId());
        Producto producto6 = new Producto("Crema de Leche Premium", "Crema de leche pasteurizada", "Contenido graso: 35%, Sin conservantes", "CLP-2024-006", empresa.getRazonSocial(), empresa.getId());

        productoRepository.saveAll(List.of(producto1, producto2, producto3, producto4, producto5, producto6));

        // TRÁMITE 1 - APROBADO (Yogurt)
        Tramite t1 = new Tramite();
        t1.setRadicadoNumber("2024-001234-56789");
        t1.setSubmissionDate(LocalDate.of(2024, 3, 15));
        t1.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t1.setProductName("Yogurt Natural Premium");
        t1.setCurrentStatus(EstadoTramite.APROBADO);
        t1.setLastUpdate(LocalDateTime.now().minusDays(10));
        tramiteRepo.save(t1);

        // TRÁMITE 2 - APROBADO (Leche)
        Tramite t2 = new Tramite();
        t2.setRadicadoNumber("2024-002345-67890");
        t2.setSubmissionDate(LocalDate.of(2024, 2, 20));
        t2.setProcedureType("Registro Sanitario - Alimento de Riesgo Bajo");
        t2.setProductName("Leche Deslactosada UHT");
        t2.setCurrentStatus(EstadoTramite.APROBADO);
        t2.setLastUpdate(LocalDateTime.now().minusDays(5));
        tramiteRepo.save(t2);

        // TRÁMITE 3 - RADICADO (Queso)
        Tramite t3 = new Tramite();
        t3.setRadicadoNumber("2024-003456-78901");
        t3.setSubmissionDate(LocalDate.now().minusDays(2));
        t3.setProcedureType("Registro Sanitario - Alimento de Riesgo Alto");
        t3.setProductName("Queso Mozzarella Premium");
        t3.setCurrentStatus(EstadoTramite.RADICADO);
        t3.setLastUpdate(LocalDateTime.now().minusDays(2));
        tramiteRepo.save(t3);

        // TRÁMITE 4 - EN EVALUACIÓN TÉCNICA (Mantequilla)
        Tramite t4 = new Tramite();
        t4.setRadicadoNumber("2024-004567-89012");
        t4.setSubmissionDate(LocalDate.of(2024, 4, 10));
        t4.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t4.setProductName("Mantequilla Sin Sal");
        t4.setCurrentStatus(EstadoTramite.EN_EVALUACION_TECNICA);
        t4.setLastUpdate(LocalDateTime.now().minusDays(3));
        tramiteRepo.save(t4);

        // TRÁMITE 5 - REQUIERE INFORMACIÓN (Kumis)
        Tramite t5 = new Tramite();
        t5.setRadicadoNumber("2024-005678-90123");
        t5.setSubmissionDate(LocalDate.of(2024, 4, 5));
        t5.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t5.setProductName("Kumis Natural");
        t5.setCurrentStatus(EstadoTramite.REQUIERE_INFORMACION);
        t5.setLastUpdate(LocalDateTime.now().minusDays(1));
        tramiteRepo.save(t5);

        // TRÁMITE 6 - APROBADO (Crema de Leche, para probar historial largo y vencimiento cercano)
        Tramite t6 = new Tramite();
        t6.setRadicadoNumber("2019-006789-01234");
        t6.setSubmissionDate(LocalDate.of(2019, 11, 10));
        t6.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t6.setProductName("Crema de Leche Premium");
        t6.setCurrentStatus(EstadoTramite.APROBADO);
        t6.setLastUpdate(LocalDateTime.now().minusDays(1800)); // Hace 5 años aprox
        tramiteRepo.save(t6);

        // EVENTOS PARA TRÁMITE 1 (APROBADO)
        crearEventosCompletos(t1, eventoRepo);
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

        // EVENTOS PARA TRÁMITE 2 (APROBADO)
        crearEventosCompletos(t2, eventoRepo);
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

        // EVENTOS PARA TRÁMITE 3 (RADICADO)
        crearEventosRadicado(t3, eventoRepo);

        // EVENTOS PARA TRÁMITE 4 (EN EVALUACIÓN)
        crearEventosEnEvaluacion(t4, eventoRepo);

        // EVENTOS PARA TRÁMITE 5 (REQUIERE INFO)
        crearEventosRequiereInfo(t5, eventoRepo);

        // EVENTOS PARA TRÁMITE 6 (APROBADO ANTIGUO)
        crearEventosCompletos(t6, eventoRepo);

        // REQUERIMIENTOS SOLO PARA TRÁMITE 5
        crearRequerimientos(t5, reqRepo);

        // NOTIFICACIONES PARA TODOS
        crearNotificaciones(List.of(t1, t2, t3, t4, t5), notifRepo);

        // NOTIFICACIÓN ESPECIAL PARA REGISTRO POR VENCER
        List<Notificacion> notificacionesT6 = List.of(
                crearNotificacion(t6, TipoNotificacion.ALERT, "Registro próximo a vencer", "Su registro sanitario vence en 25 días. Inicie proceso de renovación", false),
                crearNotificacion(t6, TipoNotificacion.INFO, "Trámite aprobado", "Su trámite fue aprobado satisfactoriamente", true)
        );
        notifRepo.saveAll(notificacionesT6);

        // PREFERENCIAS PARA TODOS
        crearPreferencias(List.of(t1, t2, t3, t4, t5), prefRepo);
        crearPreferencias(List.of(t6), prefRepo);

        // RESOLUCIONES Y REGISTROS SANITARIOS PARA APROBADOS (T1, T2 y T6)
        crearResolucionYRegistro(t1, 1L, resolucionRepository, registroSanitarioRepository, false);
        crearResolucionYRegistro(t2, 2L, resolucionRepository, registroSanitarioRepository, false);
        crearResolucionYRegistro(t6, 6L, resolucionRepository, registroSanitarioRepository, true); // true para registro por vencer

        // HISTORIAL PARA TODOS
        crearHistorial(t1, historialTramiteRepository, true);
        crearHistorial(t2, historialTramiteRepository, true);
        crearHistorial(t3, historialTramiteRepository, false);
        crearHistorial(t4, historialTramiteRepository, false);
        crearHistorial(t5, historialTramiteRepository, false);
        crearHistorial(t6, historialTramiteRepository, true);

        // CREAR EVENTOS DEL CALENDARIO
        crearEventosCalendario();

        System.out.println("✅ Datos de inicialización cargados correctamente");
        System.out.println("📋 5 trámites creados con diferentes estados");
    }

    private void crearEventosCalendario() {
        List<Evento> eventosCalendario = new java.util.ArrayList<>();

        // Eventos para vencimientos de registros sanitarios
        List<RegistroSanitario> registros = registroSanitarioRepository.findAll();
        for (RegistroSanitario registro : registros) {
            PrioridadEvento prioridad = PrioridadEvento.BAJA;
            EstadoEvento estado = EstadoEvento.ACTIVO;

            // Calcular prioridad según cercanía al vencimiento
            long diasParaVencer = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    registro.getFechaVencimiento().toLocalDate()
            );

            if (diasParaVencer <= 30) {
                prioridad = PrioridadEvento.ALTA;
            } else if (diasParaVencer <= 90) {
                prioridad = PrioridadEvento.MEDIA;
            }

            if (diasParaVencer < 0) {
                estado = EstadoEvento.VENCIDO;
            }

            Evento eventoVencimiento = Evento.builder()
                    .titulo("Vencimiento Registro Sanitario - " + registro.getNumeroRegistro())
                    .descripcion("El registro sanitario " + registro.getNumeroRegistro() + " vence el " + registro.getFechaVencimiento().toLocalDate())
                    .fecha(registro.getFechaVencimiento().toLocalDate())
                    .hora(java.time.LocalTime.of(9, 0))
                    .tipo(TipoEvento.VENCIMIENTO)
                    .categoria(CategoriaEvento.REGISTRO_SANITARIO)
                    .prioridad(prioridad)
                    .estado(estado)
                    .empresaId(registro.getEmpresaId())
                    .build();

            eventosCalendario.add(eventoVencimiento);
        }

        // Eventos para plazos finales de requerimientos
        List<Requerimiento> requerimientos = reqRepo.findAll();
        for (Requerimiento requerimiento : requerimientos) {
            PrioridadEvento prioridad = PrioridadEvento.BAJA;
            EstadoEvento estado = EstadoEvento.ACTIVO;

            // Calcular prioridad según cercanía al plazo
            long diasParaPlazo = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    requerimiento.getDeadline()
            );

            if (diasParaPlazo <= 7) {
                prioridad = PrioridadEvento.ALTA;
            } else if (diasParaPlazo <= 15) {
                prioridad = PrioridadEvento.MEDIA;
            }

            if (diasParaPlazo < 0) {
                estado = EstadoEvento.VENCIDO;
            } else if (requerimiento.getStatus() == EstadoRequerimiento.RESPONDIDO) {
                estado = EstadoEvento.COMPLETADO;
            }

            Evento eventoPlazo = Evento.builder()
                    .titulo("Plazo Final Requerimiento - Trámite " + requerimiento.getTramite().getRadicadoNumber())
                    .descripcion("Plazo final para cumplir requerimiento del trámite " + requerimiento.getTramite().getRadicadoNumber() + " - " + requerimiento.getDescription())
                    .fecha(requerimiento.getDeadline())
                    .hora(java.time.LocalTime.of(17, 0))
                    .tipo(TipoEvento.PLAZO_FINAL)
                    .categoria(CategoriaEvento.TRAMITE)
                    .prioridad(prioridad)
                    .estado(estado)
                    .empresaId(1L)
                    .tramiteId(requerimiento.getTramite().getId())
                    .build();

            eventosCalendario.add(eventoPlazo);
        }

        eventoCalendarioRepository.saveAll(eventosCalendario);
        System.out.println("✅ " + eventosCalendario.size() + " eventos de calendario creados");
    }

    // ... resto de métodos existentes sin cambios ...

    private void crearEventosCompletos(Tramite tramite, EventoTramiteRepository repo) {
        List<EventoTramite> eventos = List.of(
                crearEvento(tramite, 1, "Solicitud Radicada", "Documentos recibidos y radicado asignado", tramite.getSubmissionDate(), true, false),
                crearEvento(tramite, 2, "Verificación Documental", "Revisión inicial de documentos completada", tramite.getSubmissionDate().plusDays(5), true, false),
                crearEvento(tramite, 3, "Evaluación Técnica", "Análisis técnico del producto completado", tramite.getSubmissionDate().plusDays(10), true, false),
                crearEvento(tramite, 4, "Aprobación", "Trámite aprobado satisfactoriamente", tramite.getSubmissionDate().plusDays(15), true, true)
        );
        repo.saveAll(eventos);
    }

    private void crearEventosRadicado(Tramite tramite, EventoTramiteRepository repo) {
        List<EventoTramite> eventos = List.of(
                crearEvento(tramite, 1, "Solicitud Radicada", "Documentos recibidos y radicado asignado", tramite.getSubmissionDate(), true, true),
                crearEvento(tramite, 2, "Verificación Documental", "Pendiente revisión inicial de documentos", null, false, false),
                crearEvento(tramite, 3, "Evaluación Técnica", "Pendiente análisis técnico", null, false, false),
                crearEvento(tramite, 4, "Resolución", "Pendiente emisión de resolución", null, false, false)
        );
        repo.saveAll(eventos);
    }

    private void crearEventosEnEvaluacion(Tramite tramite, EventoTramiteRepository repo) {
        List<EventoTramite> eventos = List.of(
                crearEvento(tramite, 1, "Solicitud Radicada", "Documentos recibidos y radicado asignado", tramite.getSubmissionDate(), true, false),
                crearEvento(tramite, 2, "Verificación Documental", "Revisión inicial de documentos completada", tramite.getSubmissionDate().plusDays(3), true, false),
                crearEvento(tramite, 3, "Evaluación Técnica", "Análisis técnico del producto en curso", tramite.getSubmissionDate().plusDays(7), false, true),
                crearEvento(tramite, 4, "Resolución", "Pendiente emisión de resolución", null, false, false)
        );
        repo.saveAll(eventos);
    }

    private void crearEventosRequiereInfo(Tramite tramite, EventoTramiteRepository repo) {
        List<EventoTramite> eventos = List.of(
                crearEvento(tramite, 1, "Solicitud Radicada", "Documentos recibidos y radicado asignado", tramite.getSubmissionDate(), true, false),
                crearEvento(tramite, 2, "Verificación Documental", "Revisión inicial completada - Se requiere información adicional", tramite.getSubmissionDate().plusDays(3), true, false),
                crearEvento(tramite, 3, "Requiere Información", "Esperando documentos adicionales del solicitante", tramite.getSubmissionDate().plusDays(5), false, true),
                crearEvento(tramite, 4, "Evaluación Técnica", "Pendiente análisis técnico", null, false, false)
        );
        repo.saveAll(eventos);
    }

    private EventoTramite crearEvento(Tramite tramite, int orden, String title, String description, LocalDate date, boolean completed, boolean current) {
        EventoTramite evento = new EventoTramite();
        evento.setTramite(tramite);
        evento.setTitle(title);
        evento.setDescription(description);
        evento.setDate(date);
        evento.setCompleted(completed);
        evento.setCurrentEvent(current);
        evento.setOrden(orden);
        return evento;
    }

    private void crearRequerimientos(Tramite tramite, RequerimientoRepository repo) {
        List<Requerimiento> requerimientos = List.of(
                crearRequerimiento(tramite, "REQ-2024-005678-01", "Certificado de análisis microbiológico", "Se requiere certificado actualizado de análisis microbiológico", EstadoRequerimiento.PENDIENTE),
                crearRequerimiento(tramite, "REQ-2024-005678-02", "Información nutricional detallada", "Presentar tabla nutricional completa con valores por 100g", EstadoRequerimiento.PENDIENTE),
                crearRequerimiento(tramite, "REQ-2024-005678-03", "Certificación de probióticos", "Certificar concentración y viabilidad de probióticos", EstadoRequerimiento.PENDIENTE)
        );
        repo.saveAll(requerimientos);
    }

    private Requerimiento crearRequerimiento(Tramite tramite, String number, String title, String description, EstadoRequerimiento status) {
        Requerimiento req = new Requerimiento();
        req.setTramite(tramite);
        req.setNumber(number);
        req.setTitle(title);
        req.setDescription(description);
        req.setDate(LocalDate.now().minusDays(2));
        req.setDeadline(LocalDate.now().plusDays(15));
        req.setStatus(status);
        return req;
    }

    private void crearNotificaciones(List<Tramite> tramites, NotificacionRepository repo) {
        for (Tramite tramite : tramites) {
            List<Notificacion> notificaciones = List.of(
                    crearNotificacion(tramite, TipoNotificacion.INFO, "Estado actualizado", "Su trámite ha cambiado de estado", false),
                    crearNotificacion(tramite, TipoNotificacion.INFO, "Trámite en proceso", "Su trámite está siendo procesado", true)
            );
            repo.saveAll(notificaciones);
        }
    }

    private Notificacion crearNotificacion(Tramite tramite, TipoNotificacion tipo, String title, String message, boolean read) {
        Notificacion notif = new Notificacion();
        notif.setTramite(tramite);
        notif.setType(tipo);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now().minusDays(1));
        notif.setRead(read);
        return notif;
    }

    private void crearPreferencias(List<Tramite> tramites, PreferenciasNotificacionRepository repo) {
        for (Tramite tramite : tramites) {
            PreferenciasNotificacion pref = new PreferenciasNotificacion();
            pref.setTramite(tramite);
            pref.setEmail(true);
            pref.setSms(false);
            pref.setRequirements(true);
            pref.setStatusUpdates(true);
            repo.save(pref);
        }
    }

    private void crearResolucionYRegistro(Tramite tramite, Long productoId, ResolucionRepository resRepo, RegistroSanitarioRepository regRepo, boolean proximoAVencer) {
        // Crear resolución
        LocalDateTime fechaEmision = proximoAVencer ?
                LocalDateTime.now().minusYears(5).minusDays(5) :
                LocalDateTime.now().minusDays(5);

        Resolucion resolucion = Resolucion.builder()
                .numeroResolucion("2024-INVIMA-000" + tramite.getId())
                .fechaEmision(fechaEmision)
                .autoridad("INVIMA - Instituto Nacional de Vigilancia de Medicamentos y Alimentos")
                .estado(EstadoResolucion.APROBADA)
                .observaciones("Solicitud aprobada. El producto cumple con todos los requisitos normativos.")
                .tramiteId(tramite.getId())
                .documentoUrl("/documents/resolucion-" + tramite.getId() + ".pdf")
                .fechaNotificacion(fechaEmision)
                .build();
        resRepo.save(resolucion);

        // Crear registro sanitario
        LocalDateTime fechaExpedicion = proximoAVencer ?
                LocalDateTime.now().minusYears(5) :
                LocalDateTime.now().minusDays(5);
        LocalDateTime fechaVencimiento = proximoAVencer ?
                LocalDateTime.now().plusDays(25) : // Vence en 25 días
                LocalDateTime.now().plusYears(5);

        RegistroSanitario registro = RegistroSanitario.builder()
                .numeroRegistro("RSAA21M-201900" + String.format("%02d", tramite.getId().intValue()))
                .fechaExpedicion(fechaExpedicion)
                .fechaVencimiento(fechaVencimiento)
                .productoId(productoId)
                .empresaId(1L)
                .estado(EstadoRegistro.VIGENTE)
                .resolucionId(resolucion.getId())
                .documentoUrl("/documents/registro-sanitario-" + tramite.getId() + ".pdf")
                .build();
        regRepo.save(registro);
    }

    private void crearHistorial(Tramite tramite, HistorialTramiteRepository repo, boolean completo) {
        List<HistorialTramite> historial = List.of(
                crearHistorialItem(tramite, "TRAMITE_RADICADO", "Trámite radicado exitosamente", "Sistema SEGAR", "RADICADO", tramite.getSubmissionDate().atTime(9, 0))
        );

        if (completo) {
            historial = List.of(
                    crearHistorialItem(tramite, "TRAMITE_RADICADO", "Trámite radicado exitosamente", "Sistema SEGAR", "RADICADO", tramite.getSubmissionDate().atTime(9, 0)),
                    crearHistorialItem(tramite, "VERIFICACION_DOCUMENTAL", "Verificación documental completada", "Técnico INVIMA", "VERIFICADO", tramite.getSubmissionDate().plusDays(5).atTime(14, 30)),
                    crearHistorialItem(tramite, "EVALUACION_COMPLETADA", "Evaluación técnica completada", "Especialista INVIMA", "EVALUADO", tramite.getSubmissionDate().plusDays(10).atTime(11, 0)),
                    crearHistorialItem(tramite, "RESOLUCION_EMITIDA", "Resolución de aprobación emitida", "Director Técnico INVIMA", "APROBADO", tramite.getSubmissionDate().plusDays(15).atTime(10, 30))
            );
        }

        repo.saveAll(historial);
    }

    private HistorialTramite crearHistorialItem(Tramite tramite, String accion, String descripcion, String usuario, String estado, LocalDateTime fecha) {
        return HistorialTramite.builder()
                .tramiteId(tramite.getId())
                .fecha(fecha)
                .accion(accion)
                .descripcion(descripcion)
                .usuario(usuario)
                .estado(estado)
                .build();
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
