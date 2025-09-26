package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.RadicacionSolicitudDTO;
import com.segar.backend.tramites.api.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.tramites.domain.exceptions.DocumentosIncompletosException;
import com.segar.backend.tramites.domain.exceptions.PagoInvalidoException;
import com.segar.backend.tramites.domain.exceptions.SolicitudDuplicadaException;
import com.segar.backend.tramites.domain.Solicitud;
import com.segar.backend.tramites.service.RadicacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el Paso 5: Radicación de la Solicitud
 *
 * Este controlador maneja el proceso de radicación formal de solicitudes ante INVIMA.
 * Incluye validaciones previas, generación de número de radicado y cambio de estado.
 */
@RestController
@RequestMapping("/api/radicacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RadicacionController {

    private final RadicacionServiceImpl radicacionService;

    /**
     * Endpoint principal para radicar una solicitud
     * POST /api/radicacion/solicitud
     *
     * Validaciones previas:
     * - Empresa registrada
     * - Documentos obligatorios cargados
     * - Pago registrado y aprobado
     */
    @PostMapping("/solicitud")
    public ResponseEntity<?> radicarSolicitud(@RequestBody RadicacionSolicitudDTO radicacionDTO) {
        try {
            SolicitudRadicadaResponseDTO response = radicacionService.radicarSolicitud(radicacionDTO);
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
     * Obtiene el estado de radicación de una solicitud
     * GET /api/radicacion/estado/{empresaId}
     */
    @GetMapping("/estado/{empresaId}")
    public ResponseEntity<List<Solicitud>> obtenerEstadoRadicacion(@PathVariable Long empresaId) {
        try {
            List<Solicitud> solicitudes = radicacionService.obtenerSolicitudesRadicadas(empresaId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validaciones previas para radicación
     * GET /api/radicacion/validaciones/{empresaId}
     */
    @GetMapping("/validaciones/{empresaId}")
    public ResponseEntity<?> validarPreRequisitos(@PathVariable Long empresaId) {
        try {
            var validaciones = radicacionService.validarPreRequisitos(empresaId);
            return ResponseEntity.ok(validaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_VALIDACION", e.getMessage()));
        }
    }

    /**
     * Buscar solicitud por número de radicado
     * GET /api/radicacion/consulta/{numeroRadicado}
     */
    @GetMapping("/consulta/{numeroRadicado}")
    public ResponseEntity<Solicitud> consultarPorRadicado(@PathVariable String numeroRadicado) {
        try {
            Solicitud solicitud = radicacionService.buscarPorNumeroRadicado(numeroRadicado);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Clase interna para respuestas de error
     */
    public static class ErrorResponse {
        public String codigo;
        public String mensaje;

        public ErrorResponse(String codigo, String mensaje) {
            this.codigo = codigo;
            this.mensaje = mensaje;
        }
    }
}
