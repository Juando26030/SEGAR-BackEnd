package com.segar.backend.controllers;

import com.segar.backend.dto.*;
import com.segar.backend.services.DocumentInstanceService;
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

    private final DocumentInstanceService documentInstanceService;

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

    @Operation(summary = "Crear nueva instancia de documento",
               description = "Crea una nueva instancia de documento basada en una plantilla")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Instancia creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de instancia inválidos"),
        @ApiResponse(responseCode = "404", description = "Trámite o plantilla no encontrados"),
        @ApiResponse(responseCode = "409", description = "Ya existe una instancia para esta plantilla"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> createInstance(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "Datos de la nueva instancia", required = true)
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

    @Operation(summary = "Actualizar instancia existente",
               description = "Actualiza los datos de una instancia de documento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Instancia actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de instancia inválidos"),
        @ApiResponse(responseCode = "404", description = "Instancia no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> updateInstance(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "ID de la instancia", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la instancia", required = true)
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

    @Operation(summary = "Subir archivos a instancia",
               description = "Sube uno o más archivos asociados a una instancia de documento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivos subidos exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivos inválidos o no permitidos"),
        @ApiResponse(responseCode = "404", description = "Instancia no encontrada"),
        @ApiResponse(responseCode = "413", description = "Archivo excede el tamaño máximo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentInstanceDTO> uploadFiles(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "ID de la instancia", required = true)
            @PathVariable Long id,
            @Parameter(description = "Archivos a subir", required = true)
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

    @Operation(summary = "Exportar instancia a PDF",
               description = "Genera y almacena un PDF a partir de los datos de la instancia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Instancia sin datos suficientes para exportar"),
        @ApiResponse(responseCode = "404", description = "Instancia no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error generando PDF")
    })
    @PostMapping("/{id}/export-pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentExportResponseDTO> exportToPdf(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "ID de la instancia", required = true)
            @PathVariable Long id,
            @Parameter(description = "Opciones de exportación")
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

    @Operation(summary = "Eliminar instancia de documento",
               description = "Elimina una instancia de documento y sus archivos asociados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Instancia eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Instancia no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInstance(
            @Parameter(description = "ID del trámite", required = true)
            @PathVariable Long tramiteId,
            @Parameter(description = "ID de la instancia", required = true)
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

    @Operation(summary = "Obtener resumen de completitud de documentos",
               description = "Obtiene estadísticas de completitud de documentos para un trámite")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Trámite no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/completion-summary")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DocumentCompletionSummaryDTO> getCompletionSummary(
            @Parameter(description = "ID del trámite", required = true)
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
