package com.segar.backend.documentos.api;

import com.segar.backend.documentos.api.dto.*;
import com.segar.backend.documentos.domain.*;
import com.segar.backend.documentos.service.DocumentTemplateServiceImpl;
import com.segar.backend.shared.domain.CategoriaRiesgo;
import com.segar.backend.shared.domain.TipoTramite;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de plantillas de documentos dinámicos
 * Expone endpoints para CRUD de plantillas según especificaciones INVIMA
 */
@RestController
@RequestMapping("/api/document-templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Plantillas de Documentos", description = "API para gestión de plantillas de documentos dinámicos")
public class DocumentTemplateController {

    private final DocumentTemplateServiceImpl documentTemplateService;

    @Operation(summary = "Obtener todas las plantillas activas",
               description = "Lista todas las plantillas de documentos activas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de plantillas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<DocumentTemplateDTO>> getAllActiveTemplates() {
        log.info("GET /api/document-templates - Obteniendo todas las plantillas activas");

        try {
            List<DocumentTemplateDTO> templates = documentTemplateService.getAllActiveTemplates();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Error obteniendo plantillas activas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener plantilla por ID",
               description = "Obtiene los detalles completos de una plantilla específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plantilla encontrada"),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocumentTemplateDTO> getTemplateById(
            @Parameter(description = "ID de la plantilla", required = true)
            @PathVariable Long id) {
        log.info("GET /api/document-templates/{} - Obteniendo plantilla por ID", id);

        try {
            DocumentTemplateDTO template = documentTemplateService.getTemplateById(id);
            return ResponseEntity.ok(template);
        } catch (RuntimeException e) {
            log.error("Plantilla no encontrada con ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo plantilla por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener plantillas por tipo de trámite",
               description = "Lista plantillas aplicables a un tipo específico de trámite")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plantillas encontradas para el trámite"),
        @ApiResponse(responseCode = "400", description = "Tipo de trámite inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/by-tramite/{tipoTramite}")
    public ResponseEntity<List<DocumentTemplateDTO>> getTemplatesByTramite(
            @Parameter(description = "Tipo de trámite", required = true)
            @PathVariable TipoTramite tipoTramite) {
        log.info("GET /api/document-templates/by-tramite/{} - Obteniendo plantillas por trámite", tipoTramite);

        try {
            List<DocumentTemplateDTO> templates = documentTemplateService.getTemplatesByTramite(tipoTramite);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Error obteniendo plantillas por trámite: {}", tipoTramite, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener plantillas por trámite y categoría de riesgo",
               description = "Lista plantillas aplicables a un tipo de trámite y categoría de riesgo específicos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plantillas encontradas"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/by-tramite-riesgo")
    public ResponseEntity<List<DocumentTemplateDTO>> getTemplatesByTramiteAndRiesgo(
            @Parameter(description = "Tipo de trámite", required = true)
            @RequestParam TipoTramite tipoTramite,
            @Parameter(description = "Categoría de riesgo", required = true)
            @RequestParam CategoriaRiesgo categoriaRiesgo) {
        log.info("GET /api/document-templates/by-tramite-riesgo - Trámite: {}, Riesgo: {}",
                tipoTramite, categoriaRiesgo);

        try {
            List<DocumentTemplateDTO> templates = documentTemplateService
                    .getTemplatesByTramiteAndRiesgo(tipoTramite, categoriaRiesgo);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Error obteniendo plantillas por trámite y riesgo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener plantillas obligatorias por trámite",
               description = "Lista solo las plantillas obligatorias para un tipo de trámite")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plantillas obligatorias encontradas"),
        @ApiResponse(responseCode = "400", description = "Tipo de trámite inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/required-by-tramite/{tipoTramite}")
    public ResponseEntity<List<DocumentTemplateDTO>> getRequiredTemplatesByTramite(
            @Parameter(description = "Tipo de trámite", required = true)
            @PathVariable TipoTramite tipoTramite) {
        log.info("GET /api/document-templates/required-by-tramite/{} - Obteniendo plantillas obligatorias", tipoTramite);

        try {
            List<DocumentTemplateDTO> templates = documentTemplateService
                    .getRequiredTemplatesByTramite(tipoTramite);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Error obteniendo plantillas obligatorias por trámite: {}", tipoTramite, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Crear nueva plantilla",
               description = "Crea una nueva plantilla de documento. Solo disponible para administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plantilla creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de plantilla inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para crear plantillas"),
        @ApiResponse(responseCode = "409", description = "Código de plantilla duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentTemplateDTO> createTemplate(
            @Parameter(description = "Datos de la nueva plantilla", required = true)
            @Valid @RequestBody DocumentTemplateDTO templateDTO) {
        log.info("POST /api/document-templates - Creando nueva plantilla: {}", templateDTO.getName());

        try {
            DocumentTemplateDTO createdTemplate = documentTemplateService.createTemplate(templateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
        } catch (IllegalArgumentException e) {
            log.error("Datos inválidos para crear plantilla", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("código")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            log.error("Error creando plantilla", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar plantilla existente",
               description = "Actualiza una plantilla existente. Solo disponible para administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plantilla actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de plantilla inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para actualizar plantillas"),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada"),
        @ApiResponse(responseCode = "409", description = "Código de plantilla duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentTemplateDTO> updateTemplate(
            @Parameter(description = "ID de la plantilla a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la plantilla", required = true)
            @Valid @RequestBody DocumentTemplateDTO templateDTO) {
        log.info("PUT /api/document-templates/{} - Actualizando plantilla", id);

        try {
            DocumentTemplateDTO updatedTemplate = documentTemplateService.updateTemplate(id, templateDTO);
            return ResponseEntity.ok(updatedTemplate);
        } catch (IllegalArgumentException e) {
            log.error("Datos inválidos para actualizar plantilla", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("código")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            log.error("Error actualizando plantilla", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Desactivar plantilla",
               description = "Desactiva una plantilla existente. Solo disponible para administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plantilla desactivada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para desactivar plantillas"),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateTemplate(
            @Parameter(description = "ID de la plantilla a desactivar", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/document-templates/{} - Desactivando plantilla", id);

        try {
            documentTemplateService.deactivateTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error desactivando plantilla", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
