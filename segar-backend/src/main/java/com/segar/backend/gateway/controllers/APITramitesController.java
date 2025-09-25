package com.segar.backend.gateway.controllers;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.segar.backend.tramites.api.dto.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APITramitesController {

    HttpClient client = HttpClient.newHttpClient();

    @GetMapping("/tramites/{id}/completo")
    public ResponseEntity<TramiteCompletoDTO> obtenerTramiteCompleto(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8090/tramites/"+ id +"/completo"))
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            // Si usas fechas Java 8, registra el módulo:
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            TramiteCompletoDTO tramite = mapper.readValue(response.body(), TramiteCompletoDTO.class);

            return ResponseEntity.status(response.statusCode()).body(tramite);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/tramites/{id}/resolucion")
    public ResponseEntity<ResolucionDTO> obtenerResolucion(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8090/tramites/"+ id +"/resolucion"))
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            // Si usas fechas Java 8, registra el módulo:
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            ResolucionDTO resolucion = mapper.readValue(response.body(), ResolucionDTO.class);

            return ResponseEntity.status(response.statusCode()).body(resolucion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tramites/{id}/registro")
    public ResponseEntity<RegistroSanitarioDTO> obtenerRegistroSanitario(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8090/tramites/"+ id +"/registro"))
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            // Si usas fechas Java 8, registra el módulo:
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            RegistroSanitarioDTO registro = mapper.readValue(response.body(), RegistroSanitarioDTO.class);

            return ResponseEntity.status(response.statusCode()).body(registro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tramites/{id}/historial")
    public ResponseEntity<List<HistorialTramiteDTO>> obtenerHistorial(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/historial"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            List<HistorialTramiteDTO> historial = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, HistorialTramiteDTO.class)
            );

                return ResponseEntity.status(response.statusCode()).body(historial);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/tramites/{id}/generar-resolucion")
    public ResponseEntity<ResolucionDTO> generarResolucion(
                @PathVariable Long id,
                @RequestBody GenerarResolucionRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            String requestBody = mapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/generar-resolucion"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            ResolucionDTO resolucion = mapper.readValue(response.body(), ResolucionDTO.class);

            return ResponseEntity.status(response.statusCode()).body(resolucion);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/tramites/{id}/finalizar")
    public ResponseEntity<TramiteCompletoDTO> finalizarTramite(@PathVariable Long id) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/finalizar"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            TramiteCompletoDTO tramite = mapper.readValue(response.body(), TramiteCompletoDTO.class);

            return ResponseEntity.status(response.statusCode()).body(tramite);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tramites/tracking")
    public ResponseEntity<TrackingDTO> tracking(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/tracking"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            TrackingDTO tracking = mapper.readValue(response.body(), TrackingDTO.class);
            return ResponseEntity.status(response.statusCode()).body(tracking);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tramites/timeline")
    public ResponseEntity<List<TimelineEventDTO>> timeline(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/timeline"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            List<TimelineEventDTO> timeline = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, TimelineEventDTO.class)
            );
            return ResponseEntity.status(response.statusCode()).body(timeline);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/tramites/refresh-status")
    public ResponseEntity<TrackingDTO> refresh(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/refresh-status"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            TrackingDTO tracking = mapper.readValue(response.body(), TrackingDTO.class);
            return ResponseEntity.status(response.statusCode()).body(tracking);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tramites/requerimientos")
    public ResponseEntity<List<RequirementDTO>> requerimientos(@PathVariable Long id, @RequestParam(required = false) String estado) {
        try {
            String url = "http://localhost:8090/tramites/" + id + "/requerimientos";
            if (estado != null) {
                url += "?estado=" + estado;
            }
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            List<RequirementDTO> requerimientos = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, RequirementDTO.class)
            );
            return ResponseEntity.status(response.statusCode()).body(requerimientos);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/tramites/requerimientos/{reqId}")
    public ResponseEntity<RequirementDTO> requerimiento(@PathVariable Long id, @PathVariable Long reqId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/requerimientos/" + reqId))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            RequirementDTO requerimiento = mapper.readValue(response.body(), RequirementDTO.class);
            return ResponseEntity.status(response.statusCode()).body(requerimiento);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/tramites/requerimientos/{reqId}/respuesta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void responder(
            @PathVariable Long id,
            @PathVariable Long reqId,
            @RequestPart("mensaje") String mensaje,
            @RequestPart(name = "archivos", required = false) List<MultipartFile> archivos
    ) {
        
    }

    @GetMapping("/tramites/notificaciones")
    public ResponseEntity<List<NotificationDTO>> notificaciones(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/notificaciones"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            List<NotificationDTO> notificaciones = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, NotificationDTO.class)
            );
            return ResponseEntity.status(response.statusCode()).body(notificaciones);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/tramites/notificaciones/{notifId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarLeida(@PathVariable Long id, @PathVariable Long notifId) {
       
    }

    @GetMapping("/tramites/notificaciones/settings")
    public ResponseEntity<NotificationSettingsDTO> getSettings(@PathVariable Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/notificaciones/settings"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            NotificationSettingsDTO settings = mapper.readValue(response.body(), NotificationSettingsDTO.class);
            return ResponseEntity.status(response.statusCode()).body(settings);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/tramites/notificaciones/settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSettings(@PathVariable Long id, @RequestBody @Valid NotificationSettingsDTO dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String requestBody = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/tramites/" + id + "/notificaciones/settings"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
