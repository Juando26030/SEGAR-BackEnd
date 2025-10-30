package com.segar.backend.tramites.api;

import com.segar.backend.shared.domain.Tramite;
import com.segar.backend.tramites.api.dto.*;


import com.segar.backend.tramites.service.TramiteServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramitesController {

    private final TramiteServiceImpl service;

    @GetMapping("/all")
    public List<Tramite> obtenerTramites() {
        return service.getAllTramites();
    }

    @PostMapping("/create")
    public Tramite createTramite(@RequestBody RadicacionSolicitudDTO tramite) {
        return service.createTramite(tramite);
    }


    @GetMapping("/{id}/tracking")
    public TrackingDTO tracking(@PathVariable Long id) { return service.getTracking(id); }

    @GetMapping("/{id}/timeline")
    public List<TimelineEventDTO> timeline(@PathVariable Long id) { return service.getTimeline(id); }

    @PostMapping("/{id}/refresh-status")
    public TrackingDTO refresh(@PathVariable Long id) { return service.refreshStatus(id); }

    @GetMapping("/{id}/requerimientos")
    public List<RequirementDTO> requerimientos(@PathVariable Long id, @RequestParam(required = false) String estado) {
        return service.getRequerimientos(id, estado);
    }

    @GetMapping("/{id}/requerimientos/{reqId}")
    public RequirementDTO requerimiento(@PathVariable Long id, @PathVariable Long reqId) {
        return service.getRequerimiento(id, reqId);
    }

    @PostMapping(value = "/{id}/requerimientos/{reqId}/respuesta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void responder(
            @PathVariable Long id,
            @PathVariable Long reqId,
            @RequestPart("mensaje") String mensaje,
            @RequestPart(name = "archivos", required = false) List<MultipartFile> archivos
    ) {
        service.responderRequerimiento(id, reqId, mensaje, archivos != null ? archivos : List.of());
    }

    @GetMapping("/{id}/notificaciones")
    public List<NotificationDTO> notificaciones(@PathVariable Long id) {
        return service.getNotificaciones(id);
    }

    @PostMapping("/{id}/notificaciones/{notifId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarLeida(@PathVariable Long id, @PathVariable Long notifId) {
        service.marcarLeida(id, notifId);
    }

    @GetMapping("/{id}/notificaciones/settings")
    public NotificationSettingsDTO getSettings(@PathVariable Long id) { return service.getSettings(id); }

    @PutMapping("/{id}/notificaciones/settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSettings(@PathVariable Long id, @RequestBody @Valid NotificationSettingsDTO dto) {
        service.updateSettings(id, dto);
    }
}
