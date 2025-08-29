package com.segar.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/validaciones")
@RequiredArgsConstructor
public class ValidacionesController {

    /**
     * Valida si una empresa está registrada y activa
     * GET /api/validaciones/empresa/{empresaId}
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Map<String, Object>> validarEmpresa(@PathVariable Long empresaId) {
        // Simulación de validación de empresa
        boolean empresaValida = empresaId != null && empresaId > 0;

        Map<String, Object> validacion = Map.of(
            "empresaId", empresaId,
            "registrada", empresaValida,
            "estado", empresaValida ? "ACTIVA" : "INACTIVA",
            "mensaje", empresaValida ? "Empresa registrada y activa" : "Empresa no encontrada o inactiva"
        );

        return ResponseEntity.ok(validacion);
    }

    /**
     * Valida si todos los documentos obligatorios están cargados
     * POST /api/validaciones/documentos
     */
    @PostMapping("/documentos")
    public ResponseEntity<Map<String, Object>> validarDocumentos(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> documentosId = (List<Long>) request.get("documentosId");
        Long empresaId = Long.valueOf(request.get("empresaId").toString());

        // Simulación de validación de documentos
        boolean documentosCompletos = documentosId != null && documentosId.size() >= 3; // Mínimo 3 documentos obligatorios

        List<String> documentosFaltantes = documentosCompletos ?
            List.of() :
            List.of("Cédula de Ciudadanía", "RUT", "Certificado de Constitución");

        Map<String, Object> validacion = Map.of(
            "empresaId", empresaId,
            "documentosCompletos", documentosCompletos,
            "totalDocumentos", documentosId != null ? documentosId.size() : 0,
            "documentosFaltantes", documentosFaltantes,
            "mensaje", documentosCompletos ?
                "Todos los documentos obligatorios están cargados" :
                "Faltan documentos obligatorios por cargar"
        );

        return ResponseEntity.ok(validacion);
    }

    /**
     * Valida si existe un pago aprobado para la empresa
     * GET /api/validaciones/pago/{pagoId}
     */
    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<Map<String, Object>> validarPago(@PathVariable Long pagoId) {
        // Simulación de validación de pago
        boolean pagoValido = pagoId != null && pagoId > 0;
        String estadoPago = pagoValido ? "APROBADO" : "NO_ENCONTRADO";

        Map<String, Object> validacion = Map.of(
            "pagoId", pagoId,
            "pagoValido", pagoValido,
            "estado", estadoPago,
            "monto", pagoValido ? 150000.0 : 0.0,
            "mensaje", pagoValido ?
                "Pago encontrado y aprobado" :
                "Pago no encontrado o no aprobado"
        );

        return ResponseEntity.ok(validacion);
    }

    /**
     * Validación completa previa a la radicación
     * POST /api/validaciones/completa
     */
    @PostMapping("/completa")
    public ResponseEntity<Map<String, Object>> validacionCompleta(@RequestBody Map<String, Object> request) {
        Long empresaId = Long.valueOf(request.get("empresaId").toString());
        @SuppressWarnings("unchecked")
        List<Long> documentosId = (List<Long>) request.get("documentosId");
        Long pagoId = Long.valueOf(request.get("pagoId").toString());

        // Validaciones
        boolean empresaValida = empresaId != null && empresaId > 0;
        boolean documentosCompletos = documentosId != null && documentosId.size() >= 3;
        boolean pagoValido = pagoId != null && pagoId > 0;

        boolean puedeRadicar = empresaValida && documentosCompletos && pagoValido;

        Map<String, Object> validacionCompleta = Map.of(
            "puedeRadicar", puedeRadicar,
            "validaciones", Map.of(
                "empresa", Map.of(
                    "valida", empresaValida,
                    "mensaje", empresaValida ? "Empresa registrada" : "Empresa no válida"
                ),
                "documentos", Map.of(
                    "completos", documentosCompletos,
                    "mensaje", documentosCompletos ?
                        "Documentos obligatorios completos" :
                        "Faltan documentos obligatorios"
                ),
                "pago", Map.of(
                    "valido", pagoValido,
                    "mensaje", pagoValido ? "Pago aprobado" : "Pago no válido"
                )
            ),
            "mensaje", puedeRadicar ?
                "Todas las validaciones pasaron. Puede proceder con la radicación" :
                "No se puede radicar la solicitud. Revise las validaciones"
        );

        return ResponseEntity.ok(validacionCompleta);
    }
}
