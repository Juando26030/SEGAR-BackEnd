package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.*;
import com.segar.backend.tramites.service.HistorialTramiteServiceImpl;
import com.segar.backend.tramites.service.RegistroSanitarioServiceImpl;
import com.segar.backend.tramites.service.ResolucionServiceImpl;
import com.segar.backend.tramites.service.TramiteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para resolución de trámites INVIMA
 * Implementa los endpoints exactos requeridos por el frontend
 */
@RestController
@RequestMapping("/tramites")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TramiteResolucionController {

    private final ResolucionServiceImpl resolucionService;
    private final RegistroSanitarioServiceImpl registroSanitarioService;
    private final HistorialTramiteServiceImpl historialTramiteService;
    private final TramiteServiceImpl tramiteService;

    /**
     * 1. GET /api/tramites/{id}/resolucion
     */
    @GetMapping("/{id}/resolucion")
    public ResponseEntity<ResolucionDTO> obtenerResolucion(@PathVariable Long id) {
        try {
            ResolucionDTO resolucion = resolucionService.obtenerResolucionPorTramite(id);
            if (resolucion != null) {
                return ResponseEntity.ok(resolucion);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 2. GET /api/tramites/{id}/registro
     */
    @GetMapping("/{id}/registro")
    public ResponseEntity<RegistroSanitarioDTO> obtenerRegistroSanitario(@PathVariable Long id) {
        try {
            RegistroSanitarioDTO registro = registroSanitarioService.obtenerRegistroPorTramite(id);
            if (registro != null) {
                return ResponseEntity.ok(registro);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 3. GET /api/tramites/{id}/completo
     */
    @GetMapping("/{id}/completo")
    public ResponseEntity<TramiteCompletoDTO> obtenerTramiteCompleto(@PathVariable Long id) {
        try {
            // Obtener todos los datos del trámite
            ResolucionDTO resolucion = resolucionService.obtenerResolucionPorTramite(id);
            RegistroSanitarioDTO registro = registroSanitarioService.obtenerRegistroPorTramite(id);
            List<HistorialTramiteDTO> historial = historialTramiteService.obtenerHistorialPorTramite(id);

            // Crear el DTO completo
            TramiteCompletoDTO tramiteCompleto = TramiteCompletoDTO.builder()
                .id(id)
                .numeroRadicado("2024-001234-56789") // Esto debería venir del servicio de trámites
                .estado("APROBADO")
                .fechaCreacion(java.time.LocalDateTime.now())
                .empresaId(1L)
                .productoId(1L)
                .resolucion(resolucion)
                .registroSanitario(registro)
                .historial(historial)
                .build();

            return ResponseEntity.ok(tramiteCompleto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 4. GET /api/tramites/{id}/historial
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialTramiteDTO>> obtenerHistorial(@PathVariable Long id) {
        try {
            List<HistorialTramiteDTO> historial = historialTramiteService.obtenerHistorialPorTramite(id);
            return ResponseEntity.ok(historial != null ? historial : Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * 5. POST /api/tramites/{id}/generar-resolucion
     */
    @PostMapping("/{id}/generar-resolucion")
    public ResponseEntity<ResolucionDTO> generarResolucion(
            @PathVariable Long id,
            @RequestBody GenerarResolucionRequest request) {
        try {
            ResolucionDTO resolucion = resolucionService.generarResolucion(
                id,
                request.getDecision(),
                request.getObservaciones(),
                request.getAutoridad()
            );

            // Registrar evento en historial
            historialTramiteService.registrarEvento(
                id,
                "RESOLUCION_GENERADA",
                "Resolución " + request.getDecision() + " generada: " + resolucion.getNumeroResolucion(),
                "INVIMA",
                request.getDecision().equals("APROBAR") ? "APROBADO" : "RECHAZADO"
            );

            // Si es aprobada, generar registro sanitario automáticamente
            if (request.getDecision().equals("APROBAR")) {
                registroSanitarioService.generarRegistroSanitario(resolucion.getId(), 1L, 1L);
            }

            return ResponseEntity.ok(resolucion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 6. POST /api/tramites/{id}/finalizar
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<TramiteCompletoDTO> finalizarTramite(@PathVariable Long id) {
        try {
            // Registrar finalización en historial
            historialTramiteService.registrarEvento(
                id,
                "TRAMITE_FINALIZADO",
                "Trámite finalizado completamente",
                "Sistema",
                "FINALIZADO"
            );

            // Obtener información completa del trámite
            return obtenerTramiteCompleto(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
