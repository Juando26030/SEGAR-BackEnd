package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.*;
import com.segar.backend.tramites.service.ClasificacionTramiteService;
import com.segar.backend.tramites.service.DocumentoTramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador para la clasificación de trámites INVIMA
 * Implementa los 4 endpoints principales del sistema dinámico
 */
@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClasificacionTramiteController {

    private final ClasificacionTramiteService clasificacionService;
    private final DocumentoTramiteService documentoService;

    /**
     * Endpoint 1: Clasificar producto y obtener documentos requeridos
     * POST /api/tramites/clasificar
     */
    @PostMapping("/clasificar")
    public ResponseEntity<ResultadoClasificacionDTO> clasificarProducto(
            @Valid @RequestBody ClasificacionProductoDTO clasificacion) {

        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacion);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint 2: Guardar documento completado (con o sin archivo)
     * POST /api/tramites/{id}/documentos
     */
    @PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> guardarDocumento(
            @PathVariable Long id,
            @RequestPart("documento_id") String documentoId,
            @RequestPart("datos") String datosJson,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {

        Map<String, Object> response = documentoService.guardarDocumento(
                id, documentoId, datosJson, archivo
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 3: Validar completitud de documentos
     * GET /api/tramites/{id}/documentos/validacion
     */
    @GetMapping("/{id}/documentos/validacion")
    public ResponseEntity<ValidacionDocumentosDTO> validarCompletitud(@PathVariable Long id) {
        ValidacionDocumentosDTO validacion = documentoService.validarCompletitud(id);
        return ResponseEntity.ok(validacion);
    }

    /**
     * Endpoint 4: Radicar solicitud
     * POST /api/tramites/{id}/radicar
     */
    @PostMapping("/{id}/radicar")
    public ResponseEntity<Map<String, Object>> radicarSolicitud(@PathVariable Long id) {
        Map<String, Object> response = documentoService.radicarSolicitud(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint adicional: Obtener documentos guardados de un trámite
     * GET /api/tramites/{id}/documentos-invima
     */
    @GetMapping("/{id}/documentos-invima")
    public ResponseEntity<Map<String, Object>> obtenerDocumentos(@PathVariable Long id) {
        Map<String, Object> documentos = documentoService.obtenerDocumentos(id);
        return ResponseEntity.ok(documentos);
    }
}
