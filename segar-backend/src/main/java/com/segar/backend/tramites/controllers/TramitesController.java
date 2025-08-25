package com.segar.backend.tramites.controllers;

import com.segar.backend.tramites.dto.*;
import com.segar.backend.tramites.services.TramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tramites/{id}")
@RequiredArgsConstructor
public class TramitesController {

    private final TramiteService service;

    @GetMapping("/tracking")
    public TrackingDTO tracking(@PathVariable Long id) { return service.getTracking(id); }

    @GetMapping("/timeline")
    public List<TimelineEventDTO> timeline(@PathVariable Long id) { return service.getTimeline(id); }

    @PostMapping("/refresh-status")
    public TrackingDTO refresh(@PathVariable Long id) { return service.refreshStatus(id); }

    @GetMapping("/requerimientos")
    public List<RequirementDTO> requerimientos(@PathVariable Long id, @RequestParam(required = false) String estado) {
        return service.getRequerimientos(id, estado);
    }

    @GetMapping("/requerimientos/{reqId}")
    public RequirementDTO requerimiento(@PathVariable Long id, @PathVariable Long reqId) {
        return service.getRequerimiento(id, reqId);
    }

    @PostMapping(value = "/requerimientos/{reqId}/respuesta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void responder(
            @PathVariable Long id,
            @PathVariable Long reqId,
            @RequestPart("mensaje") String mensaje,
            @RequestPart(name = "archivos", required = false) List<MultipartFile> archivos
    ) {
        service.responderRequerimiento(id, reqId, mensaje, archivos != null ? archivos : List.of());
    }

    @GetMapping("/notificaciones")
    public List<NotificationDTO> notificaciones(@PathVariable Long id) {
        return service.getNotificaciones(id);
    }

    @PostMapping("/notificaciones/{notifId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarLeida(@PathVariable Long id, @PathVariable Long notifId) {
        service.marcarLeida(id, notifId);
    }

    @GetMapping("/notificaciones/settings")
    public NotificationSettingsDTO getSettings(@PathVariable Long id) { return service.getSettings(id); }

    @PutMapping("/notificaciones/settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSettings(@PathVariable Long id, @RequestBody @Valid NotificationSettingsDTO dto) {
        service.updateSettings(id, dto);
    }
}
