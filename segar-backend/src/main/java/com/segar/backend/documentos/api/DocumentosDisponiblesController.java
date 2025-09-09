package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentosDisponiblesController {

    @GetMapping("/disponibles")
    public ResponseEntity<List<Map<String, Object>>> getDocumentosDisponibles() {
        List<Map<String, Object>> documentos = List.of(
            Map.of(
                "id", 1,
                "nombre", "Cédula de Ciudadanía",
                "tipo", "IDENTIFICACION",
                "requerido", true,
                "descripcion", "Documento de identificación personal"
            ),
            Map.of(
                "id", 2,
                "nombre", "RUT",
                "tipo", "TRIBUTARIO",
                "requerido", true,
                "descripcion", "Registro Único Tributario"
            ),
            Map.of(
                "id", 3,
                "nombre", "Certificado de Constitución",
                "tipo", "LEGAL",
                "requerido", true,
                "descripcion", "Certificado de constitución y gerencia"
            ),
            Map.of(
                "id", 4,
                "nombre", "Estados Financieros",
                "tipo", "FINANCIERO",
                "requerido", false,
                "descripcion", "Estados financieros del último año"
            ),
            Map.of(
                "id", 5,
                "nombre", "Certificado de Calidad",
                "tipo", "TECNICO",
                "requerido", false,
                "descripcion", "Certificaciones de calidad del producto"
            )
        );

        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDocumento(@PathVariable Long id) {
        Map<String, Object> documento = Map.of(
            "id", id,
            "nombre", "Documento " + id,
            "tipo", "GENERAL",
            "requerido", true,
            "descripcion", "Descripción del documento " + id
        );

        return ResponseEntity.ok(documento);
    }
}
