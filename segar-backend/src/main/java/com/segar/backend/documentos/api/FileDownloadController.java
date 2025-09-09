package com.segar.backend.documentos.api;

import com.segar.backend.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * Controlador para descarga de archivos almacenados
 * Proporciona endpoints seguros para acceso a documentos
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Archivos", description = "API para descarga de archivos de documentos")
public class FileDownloadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Descargar archivo por clave de almacenamiento",
               description = "Descarga un archivo usando su clave de almacenamiento interno")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivo descargado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Archivo no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al archivo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/download")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Clave de almacenamiento del archivo", required = true)
            @RequestParam String storageKey) {
        log.info("GET /api/files/download?storageKey={} - Descargando archivo", storageKey);

        try {
            if (!fileStorageService.exists(storageKey)) {
                log.warn("Archivo no encontrado: {}", storageKey);
                return ResponseEntity.notFound().build();
            }

            InputStream fileStream = fileStorageService.retrieve(storageKey);
            long fileSize = fileStorageService.getFileSize(storageKey);

            // Extraer nombre del archivo de la clave de almacenamiento
            String fileName = extractFileName(storageKey);

            // Determinar tipo de contenido
            String contentType = determineContentType(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(fileStream));

        } catch (Exception e) {
            log.error("Error descargando archivo: {}", storageKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener información de archivo",
               description = "Obtiene metadatos de un archivo sin descargarlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Archivo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileInfoDTO> getFileInfo(
            @Parameter(description = "Clave de almacenamiento del archivo", required = true)
            @RequestParam String storageKey) {
        log.info("GET /api/files/info?storageKey={} - Obteniendo información de archivo", storageKey);

        try {
            if (!fileStorageService.exists(storageKey)) {
                return ResponseEntity.notFound().build();
            }

            long fileSize = fileStorageService.getFileSize(storageKey);
            String fileName = extractFileName(storageKey);
            String contentType = determineContentType(fileName);

            FileInfoDTO fileInfo = FileInfoDTO.builder()
                    .storageKey(storageKey)
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .publicUrl(fileStorageService.getPublicUrl(storageKey))
                    .build();

            return ResponseEntity.ok(fileInfo);

        } catch (Exception e) {
            log.error("Error obteniendo información de archivo: {}", storageKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractFileName(String storageKey) {
        if (storageKey.contains("/")) {
            return storageKey.substring(storageKey.lastIndexOf("/") + 1);
        }
        return storageKey;
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        return "application/octet-stream";
    }

    // DTO interno para información de archivos
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FileInfoDTO {
        private String storageKey;
        private String fileName;
        private long fileSize;
        private String contentType;
        private String publicUrl;
    }
}
