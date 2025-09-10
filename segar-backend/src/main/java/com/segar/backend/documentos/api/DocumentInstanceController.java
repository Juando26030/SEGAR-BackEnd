package com.segar.backend.documentos.api;

import com.segar.backend.documentos.api.dto.*;
import com.segar.backend.documentos.service.DocumentInstanceServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de instancias de documentos dinámicos
 * Maneja operaciones CRUD, carga de archivos y exportación a PDF
 */
@RestController
@RequestMapping("/api/tramites/{tramiteId}/document-instances")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Instancias de Documentos", description = "API para gestión de instancias de documentos en trámites")
public class DocumentInstanceController {

    private final DocumentInstanceServiceImpl documentInstanceService;

    @Operation(summary = "Obtener instancias de documentos por trámite",
               description = "Lista todas las instancias de documentos asociadas a un trámite específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de instancias obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Trámite no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al trámite"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<DocumentInstanceDTO>> getInstancesByTramite(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId) {
        log.info("GET /api/tramites/{}/document-instances - Obteniendo instancias por trámite", tramiteId);

        try {
            List<DocumentInstanceDTO> instances = documentInstanceService.getInstancesByTramite(tramiteId);
            return ResponseEntity.ok(instances);
        } catch (Exception e) {
            log.error("Error obteniendo instancias para trámite: {}", tramiteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener instancia específica por ID",
               description = "Obtiene los detalles completos de una instancia de documento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Instancia encontrada"),
        @ApiResponse(responseCode = "404", description = "Instancia no encontrada"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder a la instancia"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> getInstanceById(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "ID de la instancia", required = true)
            @PathVariable Long id) {
        log.info("GET /api/tramites/{}/document-instances/{} - Obteniendo instancia por ID", tramiteId, id);

        try {
            DocumentInstanceDTO instance = documentInstanceService.getInstanceById(id);

            // Verificar que la instancia pertenece al trámite correcto
            if (!instance.getTramiteId().equals(tramiteId)) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(instance);
        } catch (RuntimeException e) {
            log.error("Instancia no encontrada: tramite={}, instancia={}", tramiteId, id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo instancia: tramite={}, instancia={}", tramiteId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> createInstance(
            @PathVariable Long tramiteId,
            @Valid @RequestBody DocumentInstanceRequestDTO requestDTO) {
        log.info("POST /api/tramites/{}/document-instances - Creando nueva instancia", tramiteId);

        try {
            DocumentInstanceDTO createdInstance = documentInstanceService.createInstance(tramiteId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInstance);
        } catch (IllegalArgumentException e) {
            log.error("Datos inválidos para crear instancia", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("ya existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            log.error("Error creando instancia para trámite: {}", tramiteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> updateInstance(
            @PathVariable Long tramiteId,
            @PathVariable Long id,
            @Valid @RequestBody DocumentInstanceRequestDTO requestDTO) {
        log.info("PUT /api/tramites/{}/document-instances/{} - Actualizando instancia", tramiteId, id);

        try {
            DocumentInstanceDTO updatedInstance = documentInstanceService.updateInstance(tramiteId, id, requestDTO);
            return ResponseEntity.ok(updatedInstance);
        } catch (IllegalArgumentException e) {
            log.error("Datos inválidos para actualizar instancia", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no pertenece")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error actualizando instancia: tramite={}, instancia={}", tramiteId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> uploadFiles(
            @PathVariable Long tramiteId,
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) {
        log.info("POST /api/tramites/{}/document-instances/{}/upload - Subiendo {} archivos",
                tramiteId, id, files != null ? files.length : 0);

        try {
            DocumentInstanceDTO updatedInstance = documentInstanceService.uploadFiles(tramiteId, id, files);
            return ResponseEntity.ok(updatedInstance);
        } catch (IllegalArgumentException e) {
            log.error("Archivos inválidos", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no pertenece")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("tamaño máximo") || e.getMessage().contains("excede")) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }
            log.error("Error subiendo archivos: tramite={}, instancia={}", tramiteId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/export-pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentExportResponseDTO> exportToPdf(
            @PathVariable Long tramiteId,
            @PathVariable Long id,
            @RequestBody(required = false) DocumentExportRequestDTO requestDTO) {
        log.info("POST /api/tramites/{}/document-instances/{}/export-pdf - Exportando a PDF", tramiteId, id);

        if (requestDTO == null) {
            requestDTO = DocumentExportRequestDTO.builder().build();
        }

        try {
            DocumentExportResponseDTO response = documentInstanceService.exportToPdf(tramiteId, id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Datos insuficientes para exportar PDF", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no pertenece")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error exportando PDF: tramite={}, instancia={}", tramiteId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInstance(
            @PathVariable Long tramiteId,
            @PathVariable Long id) {
        log.info("DELETE /api/tramites/{}/document-instances/{} - Eliminando instancia", tramiteId, id);

        try {
            documentInstanceService.deleteInstance(tramiteId, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no pertenece")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error eliminando instancia: tramite={}, instancia={}", tramiteId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/completion-summary")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentCompletionSummaryDTO> getCompletionSummary(
            @PathVariable Long tramiteId) {
        log.info("GET /api/tramites/{}/document-instances/completion-summary - Obteniendo resumen de completitud", tramiteId);

        try {
            DocumentCompletionSummaryDTO summary = documentInstanceService.getDocumentCompletionSummary(tramiteId);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error obteniendo resumen de completitud para trámite: {}", tramiteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
