package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO;
import com.segar.backend.tramites.domain.ClasificacionProducto;
import com.segar.backend.tramites.service.ClasificacionProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clasificacion-producto")
public class ClasificacionProductoController {

    @Autowired
    private ClasificacionProductoService clasificacionService;

    @PostMapping("/{productoId}")
    public ResponseEntity<ClasificacionProducto> guardarClasificacion(
            @PathVariable Long productoId,
            @RequestBody ClasificacionProductoDTO dto) {
        ClasificacionProducto clasificacion = clasificacionService.guardarClasificacion(productoId, dto);
        return ResponseEntity.ok(clasificacion);
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<ClasificacionProducto> obtenerClasificacion(@PathVariable Long productoId) {
        ClasificacionProducto clasificacion = clasificacionService.obtenerClasificacionPorProducto(productoId);
        if (clasificacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clasificacion);
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<ClasificacionProducto> actualizarClasificacion(
            @PathVariable Long productoId,
            @RequestBody ClasificacionProductoDTO dto) {
        ClasificacionProducto clasificacion = clasificacionService.actualizarClasificacion(productoId, dto);
        return ResponseEntity.ok(clasificacion);
    }
}
