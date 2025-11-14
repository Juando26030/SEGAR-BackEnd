package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.HistorialTramiteDTO;
import com.segar.backend.tramites.api.dto.RegistroSanitarioDTO;
import com.segar.backend.tramites.api.dto.ResolucionDTO;
import com.segar.backend.tramites.api.dto.TramiteCompletoDTO;
import com.segar.backend.tramites.service.ResolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de resoluciones y cumplimiento de trámites INVIMA
 * Implementa los endpoints requeridos por el frontend Angular
 */
@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResolucionController {

    private final ResolucionService resolucionService;

    /**
     * Obtener la resolución de un trámite
     * GET /api/tramites/{id}/resolucion
     */
    @GetMapping("/{id}/resolucion")
    public ResponseEntity<ResolucionDTO> obtenerResolucion(@PathVariable Long id) {
        try {
            ResolucionDTO resolucion = resolucionService.obtenerResolucion(id);
            return ResponseEntity.ok(resolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtener el registro sanitario (solo si está aprobado)
     * GET /api/tramites/{id}/registro
     */
    @GetMapping("/{id}/registro")
    public ResponseEntity<RegistroSanitarioDTO> obtenerRegistroSanitario(@PathVariable Long id) {
        try {
            RegistroSanitarioDTO registroSanitario = resolucionService.obtenerRegistroSanitario(id);
            return ResponseEntity.ok(registroSanitario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtener información completa del trámite con resolución
     * GET /api/tramites/{id}/completo
     */
    @GetMapping("/{id}/completo")
    public ResponseEntity<TramiteCompletoDTO> obtenerTramiteCompleto(@PathVariable Long id) {
        try {
            TramiteCompletoDTO tramiteCompleto = resolucionService.obtenerTramiteCompleto(id);
            return ResponseEntity.ok(tramiteCompleto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtener historial del trámite
     * GET /api/tramites/{id}/historial
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialTramiteDTO>> obtenerHistorial(@PathVariable Long id) {
        try {
            List<HistorialTramiteDTO> historial = resolucionService.obtenerHistorial(id);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Descargar documento de resolución
     * GET /api/tramites/{id}/resolucion/descargar
     */
    @GetMapping("/{id}/resolucion/descargar")
    public ResponseEntity<Resource> descargarResolucion(@PathVariable Long id) {
        try {
            Resource resource = resolucionService.descargarResolucion(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"resolucion_" + id + ".pdf\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Descargar registro sanitario
     * GET /api/tramites/{id}/registro/descargar
     */
    @GetMapping("/{id}/registro/descargar")
    public ResponseEntity<Resource> descargarRegistroSanitario(@PathVariable Long id) {
        try {
            Resource resource = resolucionService.descargarRegistroSanitario(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"registro_sanitario_" + id + ".pdf\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Marcar trámite como finalizado
     * POST /api/tramites/{id}/finalizar
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Map<String, Object>> finalizarTramite(@PathVariable Long id) {
        try {
            resolucionService.finalizarTramite(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Trámite finalizado exitosamente");
            response.put("tramiteId", id);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("mensaje", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Manejo global de excepciones para este controlador
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("mensaje", e.getMessage());
        errorResponse.put("error", "Error interno del servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

