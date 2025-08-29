package com.segar.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.segar.backend.dto.RadicacionSolicitudDTO;
import com.segar.backend.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.exceptions.DocumentosIncompletosException;
import com.segar.backend.exceptions.PagoInvalidoException;
import com.segar.backend.exceptions.SolicitudDuplicadaException;
import com.segar.backend.models.Solicitud;
import com.segar.backend.models.EstadoSolicitud;
import com.segar.backend.services.interfaces.SolicitudService;

import java.util.List;

/**
 * Controlador REST para gestión de solicitudes de trámites INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    /**
     * Endpoint principal para radicar una nueva solicitud
     * POST /api/solicitudes/radicacion
     */
    @PostMapping("/radicacion")
    public ResponseEntity<?> radicarSolicitud(@RequestBody RadicacionSolicitudDTO radicacionDTO) {
        try {
            SolicitudRadicadaResponseDTO response = solicitudService.radicarSolicitud(radicacionDTO);
            return ResponseEntity.ok(response);

        } catch (DocumentosIncompletosException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("DOCUMENTOS_INCOMPLETOS", e.getMessage()));

        } catch (PagoInvalidoException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("PAGO_INVALIDO", e.getMessage()));

        } catch (SolicitudDuplicadaException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("SOLICITUD_DUPLICADA", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_INTERNO", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todas las solicitudes de una empresa
     * GET /api/solicitudes/empresa/{empresaId}
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesPorEmpresa(@PathVariable Long empresaId) {
        try {
            List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorEmpresa(empresaId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene solicitudes por estado
     * GET /api/solicitudes/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesPorEstado(@PathVariable EstadoSolicitud estado) {
        try {
            List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorEstado(estado);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca una solicitud por su número de radicado
     * GET /api/solicitudes/radicado/{numeroRadicado}
     */
    @GetMapping("/radicado/{numeroRadicado}")
    public ResponseEntity<Solicitud> buscarPorNumeroRadicado(@PathVariable String numeroRadicado) {
        try {
            Solicitud solicitud = solicitudService.buscarPorNumeroRadicado(numeroRadicado);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una solicitud por su ID
     * GET /api/solicitudes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerSolicitudPorId(@PathVariable Long id) {
        try {
            Solicitud solicitud = solicitudService.obtenerSolicitudPorId(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Clase interna para respuestas de error estandarizadas
     */
    public static class ErrorResponse {
        public String codigo;
        public String mensaje;

        public ErrorResponse(String codigo, String mensaje) {
            this.codigo = codigo;
            this.mensaje = mensaje;
        }

        // Getters
        public String getCodigo() { return codigo; }
        public String getMensaje() { return mensaje; }
    }
}
