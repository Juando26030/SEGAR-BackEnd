package com.segar.backend.documentos.api;

import com.segar.backend.documentos.dto.*;
import com.segar.backend.documentos.service.ClasificacionInvimaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para clasificación de productos INVIMA
 * Implementa endpoints para determinación de trámites NSO/PSA/RSA según clasificación
 */
@RestController
@RequestMapping("/api/invima")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class ClasificacionInvimaController {

    private final ClasificacionInvimaService clasificacionService;
    
    // Constantes para mensajes
    private static final String ERROR_INTERNO_SERVIDOR = "Error interno del servidor";

    /**
     * 1. POST /api/invima/determinar-tramite
     * Determina qué tipo de trámite (NSO/PSA/RSA) se requiere según la clasificación
     */
    @PostMapping("/determinar-tramite")
    public ResponseEntity<ClasificacionInvimaDTO> determinarTramite(
            @Valid @RequestBody ClasificacionInvimaRequestDTO request) {
        
        log.info("Determinando trámite INVIMA para producto: {}", request.getNombreProducto());
        
        try {
            ClasificacionInvimaDTO resultado = clasificacionService.determinarTramite(request);
            
            if (Boolean.TRUE.equals(resultado.getEsClasificacionValida())) {
                log.info("Trámite determinado exitosamente para {}: {}", 
                        request.getNombreProducto(), resultado.getTramiteRequerido());
            } else {
                log.warn("Clasificación inválida para producto {}: {}", 
                        request.getNombreProducto(), resultado.getMensajeValidacion());
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error determinando trámite para producto: {}", 
                    request.getNombreProducto(), e);
            
            ClasificacionInvimaDTO errorResponse = ClasificacionInvimaDTO.builder()
                    .esClasificacionValida(false)
                    .mensajeValidacion(ERROR_INTERNO_SERVIDOR)
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 2. GET /api/invima/formularios-disponibles
     * Lista los tipos de formularios disponibles según el trámite
     */
    @GetMapping("/formularios-disponibles")
    public ResponseEntity<List<TipoFormularioDTO>> getFormulariosDisponibles(
            @RequestParam(required = false) String tipoTramite) {
        
        log.info("Obteniendo formularios disponibles para: {}", tipoTramite);
        
        try {
            List<TipoFormularioDTO> formularios = clasificacionService.getFormulariosDisponibles(tipoTramite);
            return ResponseEntity.ok(formularios);
        } catch (Exception e) {
            log.error("Error obteniendo formularios disponibles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 3. POST /api/invima/formulario-nso
     * Procesa formulario de Notificación Sanitaria Obligatoria
     */
    @PostMapping("/formulario-nso")
    public ResponseEntity<FormularioResponseDTO> procesarFormularioNSO(
            @Valid @RequestBody FormularioNSODTO formulario) {
        
        log.info("Procesando formulario NSO para producto: {}", formulario.getNombreComercial());
        
        try {
            FormularioResponseDTO response = clasificacionService.procesarFormularioNSO(formulario);
            
            if (Boolean.TRUE.equals(response.getEsExitoso())) {
                log.info("Formulario NSO procesado exitosamente. Radicado: {}", 
                        response.getNumeroRadicado());
            } else {
                log.warn("Errores en formulario NSO: {}", response.getErrores());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error procesando formulario NSO", e);
            
            FormularioResponseDTO errorResponse = FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje(ERROR_INTERNO_SERVIDOR)
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 4. POST /api/invima/formulario-psa
     * Procesa formulario de Permiso Sanitario
     */
    @PostMapping("/formulario-psa")
    public ResponseEntity<FormularioResponseDTO> procesarFormularioPSA(
            @Valid @RequestBody FormularioPSADTO formulario) {
        
        log.info("Procesando formulario PSA para producto: {}", formulario.getNombreComercial());
        
        try {
            FormularioResponseDTO response = clasificacionService.procesarFormularioPSA(formulario);
            
            if (Boolean.TRUE.equals(response.getEsExitoso())) {
                log.info("Formulario PSA procesado exitosamente. Radicado: {}", 
                        response.getNumeroRadicado());
            } else {
                log.warn("Errores en formulario PSA: {}", response.getErrores());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error procesando formulario PSA", e);
            
            FormularioResponseDTO errorResponse = FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje(ERROR_INTERNO_SERVIDOR)
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 5. POST /api/invima/formulario-rsa
     * Procesa formulario de Registro Sanitario
     */
    @PostMapping("/formulario-rsa")
    public ResponseEntity<FormularioResponseDTO> procesarFormularioRSA(
            @Valid @RequestBody FormularioRSADTO formulario) {
        
        log.info("Procesando formulario RSA para producto: {}", formulario.getNombreComercial());
        
        try {
            FormularioResponseDTO response = clasificacionService.procesarFormularioRSA(formulario);
            
            if (Boolean.TRUE.equals(response.getEsExitoso())) {
                log.info("Formulario RSA procesado exitosamente. Radicado: {}", 
                        response.getNumeroRadicado());
            } else {
                log.warn("Errores en formulario RSA: {}", response.getErrores());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error procesando formulario RSA", e);
            
            FormularioResponseDTO errorResponse = FormularioResponseDTO.builder()
                    .esExitoso(false)
                    .mensaje(ERROR_INTERNO_SERVIDOR)
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 6. GET /api/invima/validar-clasificacion
     * Valida coherencia de la clasificación propuesta
     */
    @GetMapping("/validar-clasificacion")
    public ResponseEntity<ValidacionClasificacionDTO> validarClasificacion(
            @RequestParam String categoria,
            @RequestParam String riesgo,
            @RequestParam String poblacion,
            @RequestParam String procesamiento) {
        
        log.info("Validando clasificación: {} - {} - {} - {}", categoria, riesgo, poblacion, procesamiento);
        
        try {
            ValidacionClasificacionDTO validacion = clasificacionService.validarClasificacion(
                    categoria, riesgo, poblacion, procesamiento);
            
            return ResponseEntity.ok(validacion);
        } catch (Exception e) {
            log.error("Error validando clasificación", e);
            
            ValidacionClasificacionDTO errorResponse = ValidacionClasificacionDTO.builder()
                    .esValida(false)
                    .mensaje(ERROR_INTERNO_SERVIDOR)
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 7. GET /api/invima/documentos-requeridos
     * Obtiene lista de documentos obligatorios según el trámite
     */
    @GetMapping("/documentos-requeridos")
    public ResponseEntity<List<DocumentoRequeridoDTO>> getDocumentosRequeridos(
            @RequestParam String tipoTramite,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String poblacion) {
        
        log.info("Obteniendo documentos requeridos para trámite: {}", tipoTramite);
        
        try {
            List<DocumentoRequeridoDTO> documentos = clasificacionService.getDocumentosRequeridos(
                    tipoTramite, categoria, poblacion);
            
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            log.error("Error obteniendo documentos requeridos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 8. GET /api/invima/matriz-clasificacion
     * Obtiene la matriz completa de clasificación INVIMA
     */
    @GetMapping("/matriz-clasificacion")
    public ResponseEntity<MatrizClasificacionDTO> getMatrizClasificacion() {
        log.info("Obteniendo matriz de clasificación INVIMA");
        
        try {
            MatrizClasificacionDTO matriz = clasificacionService.getMatrizClasificacion();
            return ResponseEntity.ok(matriz);
        } catch (Exception e) {
            log.error("Error obteniendo matriz de clasificación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 9. POST /api/invima/generar-documentos
     * Genera documentos automáticos según el tipo de formulario
     */
    @PostMapping("/generar-documentos")
    public ResponseEntity<DocumentosGeneradosDTO> generarDocumentosAutomaticos(
            @RequestParam String tipoFormulario,
            @RequestBody Object datosFormulario) {
        
        log.info("Generando documentos automáticos para formulario: {}", tipoFormulario);
        
        try {
            DocumentosGeneradosDTO documentos = clasificacionService.generarDocumentosAutomaticos(
                    tipoFormulario, datosFormulario);
            
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            log.error("Error generando documentos automáticos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}