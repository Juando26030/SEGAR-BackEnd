package com.segar.backend.tramites.api;

import com.segar.backend.tramites.api.dto.*;
import com.segar.backend.tramites.service.ClasificacionTramiteService;
import com.segar.backend.tramites.service.DocumentoTramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Controlador para la clasificación de trámites INVIMA
 * Implementa los 4 endpoints principales del sistema dinámico
 */
@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClasificacionTramiteController {

    @Autowired
    private ClasificacionTramiteService clasificacionTramiteService;

    @PostMapping("/clasificacion")
    public String getTramiteClasificacion(@RequestBody ClasificationRequestDTO requestDTO) {
        
        return clasificacionTramiteService.clasificarTramite(
            requestDTO.getCategoria(),
            requestDTO.getPoblacion(),
            requestDTO.getProcesamiento()
        );
    }
    
    
}
