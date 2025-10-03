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

        // Crear producto
        Producto producto = new Producto("Yogurt Natural Premium",
                "Yogurt natural sin conservantes",
                "Contenido graso: 3.2%, Proteína: 4.5%",
                "YNP-2024-001",
                empresa.getRazonSocial(),
                empresa.getId());
        productoRepository.save(producto);

        // Tramite base
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

}
