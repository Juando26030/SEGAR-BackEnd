package com.segar.backend.shared.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresasController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getEmpresas() {
        List<Map<String, Object>> empresas = List.of(
            Map.of(
                "id", 1,
                "nit", "900123456-7",
                "razonSocial", "Empresa Ejemplo S.A.S.",
                "telefono", "3001234567",
                "email", "contacto@empresa1.com",
                "direccion", "Calle 123 #45-67, Bogotá",
                "representanteLegal", "Juan Pérez",
                "estado", "ACTIVA"
            ),
            Map.of(
                "id", 2,
                "nit", "800987654-3",
                "razonSocial", "Servicios Integrales Ltda.",
                "telefono", "3109876543",
                "email", "info@servicios.com",
                "direccion", "Carrera 50 #30-20, Medellín",
                "representanteLegal", "María García",
                "estado", "ACTIVA"
            ),
            Map.of(
                "id", 3,
                "nit", "700555666-9",
                "razonSocial", "Comercializadora del Valle S.A.",
                "telefono", "3187654321",
                "email", "ventas@comercializadora.com",
                "direccion", "Avenida 6 #25-15, Cali",
                "representanteLegal", "Carlos Rodríguez",
                "estado", "ACTIVA"
            )
        );

        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmpresa(@PathVariable Long id) {
        Map<String, Object> empresa = Map.of(
            "id", id,
            "nit", "900" + id + "00000-" + (id % 10),
            "razonSocial", "Empresa " + id + " S.A.S.",
            "telefono", "300000" + id + "000",
            "email", "empresa" + id + "@email.com",
            "direccion", "Dirección " + id,
            "representanteLegal", "Representante " + id,
            "estado", "ACTIVA"
        );

        return ResponseEntity.ok(empresa);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEmpresa(@RequestBody Map<String, Object> empresaData) {
        Map<String, Object> nuevaEmpresa = Map.of(
            "id", System.currentTimeMillis() % 1000,
            "nit", empresaData.get("nit"),
            "razonSocial", empresaData.get("razonSocial"),
            "telefono", empresaData.get("telefono"),
            "email", empresaData.get("email"),
            "direccion", empresaData.get("direccion"),
            "representanteLegal", empresaData.get("representanteLegal"),
            "estado", "ACTIVA",
            "mensaje", "Empresa creada exitosamente"
        );

        return ResponseEntity.ok(nuevaEmpresa);
    }
}
