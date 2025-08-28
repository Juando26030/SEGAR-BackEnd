package com.segar.backend.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import com.segar.backend.models.EstadoRequerimiento;
import com.segar.backend.models.EstadoTramite;
import com.segar.backend.models.EventoTramite;
import com.segar.backend.models.Notificacion;
import com.segar.backend.models.PreferenciasNotificacion;
import com.segar.backend.models.Producto;
import com.segar.backend.models.Requerimiento;
import com.segar.backend.models.TipoNotificacion;
import com.segar.backend.models.Tramite;
import com.segar.backend.repositories.EventoTramiteRepository;
import com.segar.backend.repositories.NotificacionRepository;
import com.segar.backend.repositories.PreferenciasNotificacionRepository;
import com.segar.backend.repositories.ProductoRepository;
import com.segar.backend.repositories.RequerimientoRepository;
import com.segar.backend.repositories.TramiteRepository;

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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        productoRepository.save(new Producto("Producto 1", "Descripción del producto 1", "Especificaciones del producto 1", "Referencia del producto 1", "Fabricante del producto 1"));

        // Tramite base
        Tramite t = new Tramite();
        t.setRadicadoNumber("2024-001234-56789");
        t.setSubmissionDate(LocalDate.of(2024,3,15));
        t.setProcedureType("Registro Sanitario - Alimento de Riesgo Medio");
        t.setProductName("Yogurt Natural Premium ");
        t.setCurrentStatus(EstadoTramite.EN_EVALUACION_TECNICA);
        t.setLastUpdate(LocalDateTime.now());
        tramiteRepo.save(t);

        // Eventos timeline
        EventoTramite e1 = new EventoTramite(); e1.setTramite(t); e1.setTitle("Solicitud Radicada"); e1.setDescription("Documentos recibidos y radicado asignado"); e1.setDate(LocalDate.of(2024,3,15)); e1.setCompleted(true); e1.setCurrentEvent(false); e1.setOrden(1);
        EventoTramite e2 = new EventoTramite(); e2.setTramite(t); e2.setTitle("Verificación Documental"); e2.setDescription("Revisión inicial de documentos completada"); e2.setDate(LocalDate.of(2024,3,20)); e2.setCompleted(true); e2.setCurrentEvent(false); e2.setOrden(2);
        EventoTramite e3 = new EventoTramite(); e3.setTramite(t); e3.setTitle("Evaluación Técnica"); e3.setDescription("Análisis técnico del producto en curso"); e3.setDate(LocalDate.of(2024,3,25)); e3.setCompleted(false); e3.setCurrentEvent(true); e3.setOrden(3);
        eventoRepo.saveAll(List.of(e1,e2,e3));

        // Requerimientos
        Requerimiento r1 = new Requerimiento();
        r1.setTramite(t); r1.setNumber("REQ-2024-001234-01");
        r1.setTitle("Información nutricional complementaria");
        r1.setDescription("Se requiere información adicional...");
        r1.setDate(LocalDate.of(2024,4,2));
        r1.setDeadline(LocalDate.now().plusDays(12));
        r1.setStatus(EstadoRequerimiento.PENDIENTE);
        reqRepo.save(r1);

        Requerimiento r2 = new Requerimiento();
        r2.setTramite(t); r2.setNumber("REQ-2024-001234-02");
        r2.setTitle("Clarificación proceso de fabricación");
        r2.setDescription("Descripción detallada del proceso de pasteurización");
        r2.setDate(LocalDate.of(2024,3,28));
        r2.setDeadline(LocalDate.of(2024,4,5));
        r2.setStatus(EstadoRequerimiento.RESPONDIDO);
        reqRepo.save(r2);

        Requerimiento r3 = new Requerimiento();
        r3.setTramite(t); r3.setNumber("REQ-2024-001234-03");
        r3.setTitle("Certificados de análisis de laboratorio");
        r3.setDescription("El scrum master es muy gay");
        r3.setDate(LocalDate.of(2024,3,22));
        r3.setDeadline(LocalDate.of(2024,3,30));
        r3.setStatus(EstadoRequerimiento.PENDIENTE);
        reqRepo.save(r3);




        // Notificaciones
        Notificacion n1 = new Notificacion();
        n1.setTramite(t); n1.setType(TipoNotificacion.REQUIREMENT);
        n1.setTitle("Nuevo requerimiento recibido");
        n1.setMessage("Se ha generado un nuevo requerimiento...");
        n1.setDate(LocalDateTime.of(2024,4,2,10,0)); n1.setRead(false);
        Notificacion n2 = new Notificacion();
        n2.setTramite(t); n2.setType(TipoNotificacion.STATUS);
        n2.setTitle("Cambio de estado del trámite");
        n2.setMessage("Su trámite ha pasado a evaluación técnica.");
        n2.setDate(LocalDateTime.of(2024,3,25,9,0)); n2.setRead(true);
        notifRepo.saveAll(List.of(n1,n2));

        // Preferencias
        PreferenciasNotificacion pref = new PreferenciasNotificacion();
        pref.setTramite(t); pref.setEmail(true); pref.setSms(false); pref.setRequirements(true); pref.setStatusUpdates(true);
        prefRepo.save(pref);
    }
    
}
