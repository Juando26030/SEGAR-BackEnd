package com.segar.backend.tramites.service;




import com.segar.backend.shared.domain.EstadoTramite;
import com.segar.backend.shared.domain.EstadoRequerimiento;
import com.segar.backend.shared.domain.TipoNotificacion;
import com.segar.backend.shared.infrastructure.TramiteRepository;
import com.segar.backend.tramites.domain.*;
import com.segar.backend.tramites.api.dto.NotificationDTO;
import com.segar.backend.tramites.api.dto.NotificationSettingsDTO;
import com.segar.backend.shared.domain.Tramite;
import com.segar.backend.tramites.api.dto.RequirementDTO;
import com.segar.backend.tramites.api.dto.TimelineEventDTO;
import com.segar.backend.tramites.api.dto.TrackingDTO;
import com.segar.backend.tramites.infrastructure.*;

import com.segar.backend.services.interfaces.TramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TramiteServiceImpl implements TramiteService {

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

    public TrackingDTO getTracking(Long tramiteId) {
        Tramite t = tramiteRepo.findById(tramiteId).orElseThrow();
        long days = ChronoUnit.DAYS.between(t.getSubmissionDate(), LocalDate.now());
        return new TrackingDTO(
                t.getRadicadoNumber(), t.getSubmissionDate(), t.getProcedureType(),
                t.getProductName(), toFrontStatus(t.getCurrentStatus()), days
        );
    }

    public List<TimelineEventDTO> getTimeline(Long tramiteId) {
        return eventoRepo.findByTramiteIdOrderByOrdenAsc(tramiteId).stream()
                .map(e -> new TimelineEventDTO(
                        e.getId(), e.getTitle(), e.getDescription(),
                        e.getDate() != null ? e.getDate().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es","CO"))) : "Pendiente",
                        e.isCompleted(), e.isCurrentEvent()
                )).toList();
    }

    @Transactional
    public TrackingDTO refreshStatus(Long tramiteId) {
        Tramite t = tramiteRepo.findById(tramiteId).orElseThrow();
        EstadoTramite next = switch (t.getCurrentStatus()) {
            case RADICADO -> EstadoTramite.EN_EVALUACION_TECNICA;
            case EN_EVALUACION_TECNICA -> EstadoTramite.REQUIERE_INFORMACION;
            case REQUIERE_INFORMACION -> EstadoTramite.APROBADO;
            default -> t.getCurrentStatus();
        };
        if (next != t.getCurrentStatus()) {
            t.setCurrentStatus(next);
            t.setLastUpdate(LocalDateTime.now());
            List<EventoTramite> eventos = eventoRepo.findByTramiteIdOrderByOrdenAsc(tramiteId);
            eventos.forEach(ev -> ev.setCurrentEvent(false));
            eventoRepo.saveAll(eventos);
        }
        return getTracking(tramiteId);
    }

    public List<RequirementDTO> getRequerimientos(Long tramiteId, @Nullable String estadoFiltro) {
        return reqRepo.findByTramiteId(tramiteId).stream()
                .filter(r -> estadoFiltro == null || r.getStatus().name().equalsIgnoreCase(estadoFiltro))
                .map(this::toRequirementDTO)
                .toList();
    }

    public RequirementDTO getRequerimiento(Long tramiteId, Long reqId) {
        Requerimiento r = reqRepo.findById(reqId).filter(x -> x.getTramite().getId().equals(tramiteId)).orElseThrow();
        return toRequirementDTO(r);
    }

    @Transactional
    public void responderRequerimiento(Long tramiteId, Long reqId, String mensaje, List<MultipartFile> archivos) {
        Requerimiento r = reqRepo.findById(reqId).filter(x -> x.getTramite().getId().equals(tramiteId)).orElseThrow();
        RespuestaRequerimiento resp = new RespuestaRequerimiento();
        resp.setRequerimiento(r);
        resp.setFecha(LocalDateTime.now());
        resp.setMensaje(mensaje);

        List<Archivo> saved = new ArrayList<>();
        for (MultipartFile f : archivos) {
            if (f == null || f.isEmpty()) continue;
            Archivo a = new Archivo();
            a.setNombre(f.getOriginalFilename());
            a.setTipoMime(f.getContentType());
            a.setTamano(f.getSize());
            a.setOwnerRespuesta(resp);
            saved.add(a);
        }
        resp.setArchivos(saved);
        r.getRespuestas().add(resp);
        r.setStatus(EstadoRequerimiento.RESPONDIDO);

        Notificacion n = new Notificacion();
        n.setTramite(r.getTramite());
        n.setType(TipoNotificacion.REQUIREMENT);
        n.setTitle("Requerimiento respondido");
        n.setMessage("Se registró una respuesta al requerimiento " + r.getNumber());
        n.setDate(LocalDateTime.now());
        n.setRead(false);
        notifRepo.save(n);
    }

    public List<NotificationDTO> getNotificaciones(Long tramiteId) {
        return notifRepo.findByTramiteIdOrderByDateDesc(tramiteId).stream()
                .map(n -> new NotificationDTO(
                        n.getId(), n.getType().name().toLowerCase(), n.getTitle(), n.getMessage(),
                        n.getDate().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es","CO"))),
                        n.isRead()
                )).toList();
    }

    @Transactional
    public void marcarLeida(Long tramiteId, Long notifId) {
        Notificacion n = notifRepo.findById(notifId).orElseThrow();
        if (!n.getTramite().getId().equals(tramiteId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        n.setRead(true);
    }

    public NotificationSettingsDTO getSettings(Long tramiteId) {
        PreferenciasNotificacion p = prefRepo.findByTramiteId(tramiteId).orElseGet(() -> {
            PreferenciasNotificacion np = new PreferenciasNotificacion();
            np.setTramite(tramiteRepo.findById(tramiteId).orElseThrow());
            np.setEmail(true); np.setRequirements(true); np.setStatusUpdates(true); np.setSms(false);
            return prefRepo.save(np);
        });
        return new NotificationSettingsDTO(p.isEmail(), p.isSms(), p.isRequirements(), p.isStatusUpdates());
    }

    @Transactional
    public void updateSettings(Long tramiteId, NotificationSettingsDTO dto) {
        PreferenciasNotificacion p = prefRepo.findByTramiteId(tramiteId).orElseThrow();
        p.setEmail(dto.email()); p.setSms(dto.sms()); p.setRequirements(dto.requirements()); p.setStatusUpdates(dto.statusUpdates());
    }

    public String toFrontStatus(EstadoTramite e) {
        return switch (e) {
            case RADICADO -> "Radicado";
            case EN_EVALUACION_TECNICA -> "En evaluación técnica";
            case REQUIERE_INFORMACION -> "Requiere información";
            case APROBADO -> "Aprobado";
            case RECHAZADO -> "Rechazado";
        };
    }

    public RequirementDTO toRequirementDTO(Requerimiento r) {
        int daysRemaining = r.getDeadline() != null ? (int) ChronoUnit.DAYS.between(LocalDate.now(), r.getDeadline()) : 0;
        return new RequirementDTO(
                r.getId(), r.getNumber(), r.getTitle(), r.getDescription(),
                Math.max(daysRemaining, 0),
                switch (r.getStatus()) {
                    case PENDIENTE -> "Pendiente";
                    case RESPONDIDO -> "Respondido";
                    case VENCIDO -> "Vencido";
                },
                r.getDate() != null ? r.getDate().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es","CO"))) : ""
        );
    }
}
