package com.segar.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosController {

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Map<String, Object>>> getPagosPorEstado(@PathVariable String estado) {
        List<Map<String, Object>> pagos = List.of(
            Map.of(
                "id", 1,
                "numeroTransaccion", "TXN001234567",
                "monto", 150000.0,
                "moneda", "COP",
                "estado", estado,
                "metodoPago", "TARJETA_CREDITO",
                "fechaPago", LocalDateTime.now().toString(),
                "descripcion", "Pago por trámite de registro sanitario",
                "empresaId", 1,
                "tramiteId", 1
            ),
            Map.of(
                "id", 2,
                "numeroTransaccion", "TXN001234568",
                "monto", 200000.0,
                "moneda", "COP",
                "estado", estado,
                "metodoPago", "PSE",
                "fechaPago", LocalDateTime.now().minusDays(1).toString(),
                "descripcion", "Pago por renovación de registro",
                "empresaId", 2,
                "tramiteId", 2
            ),
            Map.of(
                "id", 3,
                "numeroTransaccion", "TXN001234569",
                "monto", 300000.0,
                "moneda", "COP",
                "estado", estado,
                "metodoPago", "TRANSFERENCIA",
                "fechaPago", LocalDateTime.now().minusDays(2).toString(),
                "descripcion", "Pago por modificación de registro",
                "empresaId", 3,
                "tramiteId", 3
            )
        );

        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPago(@PathVariable Long id) {
        Map<String, Object> pago = Map.of(
            "id", id,
            "numeroTransaccion", "TXN00123456" + id,
            "monto", 150000.0 + (id * 10000),
            "moneda", "COP",
            "estado", "APROBADO",
            "metodoPago", "TARJETA_CREDITO",
            "fechaPago", LocalDateTime.now().toString(),
            "descripcion", "Pago por trámite " + id,
            "empresaId", id,
            "tramiteId", id
        );

        return ResponseEntity.ok(pago);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody Map<String, Object> pagoData) {
        Map<String, Object> pagoResult = Map.of(
            "id", System.currentTimeMillis() % 1000,
            "numeroTransaccion", "TXN" + System.currentTimeMillis(),
            "monto", pagoData.get("monto"),
            "moneda", pagoData.getOrDefault("moneda", "COP"),
            "estado", "APROBADO",
            "metodoPago", pagoData.get("metodoPago"),
            "fechaPago", LocalDateTime.now().toString(),
            "descripcion", pagoData.get("descripcion"),
            "mensaje", "Pago procesado exitosamente"
        );

        return ResponseEntity.ok(pagoResult);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPagos() {
        return getPagosPorEstado("APROBADO");
    }
}
