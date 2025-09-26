package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tramites/{id}")
@RequiredArgsConstructor
public class DocumentosController {

    @GetMapping("/documentos")
    public List<Map<String,Object>> documentos(@PathVariable Long id) {
        return List.of(
                Map.of("id", 1, "nombre", "Radicado.pdf", "tipo", "PDF", "fecha", LocalDate.now().toString(), "tamano", 12345, "urlDescarga", "/api/tramites/%d/documentos/1/descargar".formatted(id))
        );
    }

    @GetMapping(value="/certificado", produces= MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> certificado(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
