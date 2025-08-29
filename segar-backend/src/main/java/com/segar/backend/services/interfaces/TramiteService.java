package com.segar.backend.services.interfaces;

import com.segar.backend.models.EstadoTramite;
import com.segar.backend.models.Requerimiento;
import com.segar.backend.models.DTOs.NotificationDTO;
import com.segar.backend.models.DTOs.NotificationSettingsDTO;
import com.segar.backend.models.DTOs.RequirementDTO;
import com.segar.backend.models.DTOs.TimelineEventDTO;
import com.segar.backend.models.DTOs.TrackingDTO;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public interface TramiteService {

    public TrackingDTO getTracking(Long tramiteId);

    public List<TimelineEventDTO> getTimeline(Long tramiteId);

    @Transactional
    public TrackingDTO refreshStatus(Long tramiteId);
    public List<RequirementDTO> getRequerimientos(Long tramiteId, @Nullable String estadoFiltro);

    public RequirementDTO getRequerimiento(Long tramiteId, Long reqId);

    @Transactional
    public void responderRequerimiento(Long tramiteId, Long reqId, String mensaje, List<MultipartFile> archivos);

    public List<NotificationDTO> getNotificaciones(Long tramiteId);

    @Transactional
    public void marcarLeida(Long tramiteId, Long notifId);

    public NotificationSettingsDTO getSettings(Long tramiteId);

    @Transactional
    public void updateSettings(Long tramiteId, NotificationSettingsDTO dto);

}
