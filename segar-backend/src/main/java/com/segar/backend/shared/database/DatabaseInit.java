package com.segar.backend.shared.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.segar.backend.shared.domain.*;
import com.segar.backend.tramites.domain.*;
import com.segar.backend.tramites.infrastructure.*;
import com.segar.backend.shared.infrastructure.*;
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

    @Autowired
    private EmpresaRepository empresaRepository;


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

        // EVENTOS PARA TRÁMITE 2 (APROBADO)
        crearEventosCompletos(t2, eventoRepo);

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

        System.out.println("✅ Datos de inicialización cargados correctamente");
        System.out.println("📋 5 trámites creados con diferentes estados");
    }

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


}
