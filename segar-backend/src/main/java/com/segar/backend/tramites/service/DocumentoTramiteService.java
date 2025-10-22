package com.segar.backend.tramites.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.segar.backend.tramites.api.dto.ValidacionDocumentosDTO;
import com.segar.backend.tramites.domain.Solicitud;
import com.segar.backend.tramites.domain.TramiteDocumento;
import com.segar.backend.tramites.infrastructure.SolicitudRepository;
import com.segar.backend.tramites.infrastructure.TramiteDocumentoRepository;
import com.segar.backend.shared.domain.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de documentos de trámites INVIMA
 */
@Service
@RequiredArgsConstructor
public class DocumentoTramiteService {

    private final TramiteDocumentoRepository documentoRepository;
    private final SolicitudRepository solicitudRepository;
    private final ObjectMapper objectMapper;

    private static final String UPLOAD_DIR = "uploads/tramites";

    /**
     * Guarda un documento completado por el usuario
     */
    @Transactional
    public Map<String, Object> guardarDocumento(
            Long tramiteId,
            String documentoId,
            String datosJson,
            MultipartFile archivo) {

        try {
            // Verificar que la solicitud existe
            Solicitud solicitud = solicitudRepository.findById(tramiteId)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            // Parsear datos JSON
            Map<String, Object> datos = objectMapper.readValue(
                    datosJson,
                    new TypeReference<Map<String, Object>>() {}
            );

            // Buscar o crear documento
            TramiteDocumento documento = documentoRepository
                    .findBySolicitudIdAndDocumentoId(tramiteId, documentoId)
                    .orElse(TramiteDocumento.builder()
                            .solicitudId(tramiteId)
                            .documentoId(documentoId)
                            .fechaCreacion(LocalDateTime.now())
                            .build());

            // Guardar archivo si existe
            String archivoUrl = null;
            if (archivo != null && !archivo.isEmpty()) {
                archivoUrl = guardarArchivo(tramiteId, documentoId, archivo);
                documento.setArchivoUrl(archivoUrl);
                documento.setNombreArchivo(archivo.getOriginalFilename());
                documento.setTamanioArchivo(archivo.getSize());
            }

            // Actualizar documento
            documento.setDatos(objectMapper.writeValueAsString(datos));
            documento.setEstado("COMPLETO");
            documento.setProgreso(100);
            documento.setFechaActualizacion(LocalDateTime.now());

            documentoRepository.save(documento);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", archivo != null ?
                    "Documento guardado correctamente" :
                    "Documento autogenerado correctamente");

            Map<String, Object> documentoInfo = new HashMap<>();
            documentoInfo.put("id", documento.getId());
            documentoInfo.put("documento_id", documento.getDocumentoId());
            documentoInfo.put("tramite_id", tramiteId);
            documentoInfo.put("estado", documento.getEstado());
            documentoInfo.put("progreso", documento.getProgreso());
            documentoInfo.put("archivo_url", archivoUrl);
            documentoInfo.put("fecha_carga", documento.getFechaActualizacion());

            response.put("documento", documentoInfo);

            return response;

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el documento: " + e.getMessage());
        }
    }

    /**
     * Valida la completitud de los documentos de un trámite
     */
    public ValidacionDocumentosDTO validarCompletitud(Long tramiteId) {
        // Obtener todos los documentos del trámite
        List<TramiteDocumento> documentos = documentoRepository.findBySolicitudId(tramiteId);

        // Calcular estadísticas
        long completos = documentos.stream()
                .filter(d -> "COMPLETO".equals(d.getEstado()))
                .count();

        int total = documentos.size();
        int progresoGlobal = total > 0 ? (int) ((completos * 100.0) / total) : 0;

        // Obtener documentos pendientes
        List<String> pendientes = documentos.stream()
                .filter(d -> !"COMPLETO".equals(d.getEstado()))
                .map(TramiteDocumento::getDocumentoId)
                .collect(Collectors.toList());

        // Verificar errores
        List<String> errores = new ArrayList<>();
        if (total == 0) {
            errores.add("No se han registrado documentos para este trámite");
        }

        boolean puedeRadicar = completos == total && total > 0;

        return ValidacionDocumentosDTO.builder()
                .completo(puedeRadicar)
                .progresoGlobal(progresoGlobal)
                .documentosCompletos((int) completos)
                .documentosTotales(total)
                .documentosPendientes(pendientes)
                .errores(errores)
                .puedeRadicar(puedeRadicar)
                .build();
    }

    /**
     * Radica una solicitud cuando todos los documentos están completos
     */
    @Transactional
    public Map<String, Object> radicarSolicitud(Long tramiteId) {
        // Validar completitud
        ValidacionDocumentosDTO validacion = validarCompletitud(tramiteId);

        if (!validacion.getPuedeRadicar()) {
            throw new RuntimeException(
                    "No se puede radicar la solicitud. Documentos pendientes: " +
                    String.join(", ", validacion.getDocumentosPendientes())
            );
        }

        // Obtener solicitud
        Solicitud solicitud = solicitudRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Generar número de radicado
        String numeroRadicado = generarNumeroRadicado();

        // Actualizar solicitud
        solicitud.setNumeroRadicado(numeroRadicado);
        solicitud.setEstado(EstadoSolicitud.RADICADA);
        solicitud.setFechaRadicacion(LocalDateTime.now());

        solicitudRepository.save(solicitud);

        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Solicitud radicada exitosamente");
        response.put("numero_radicado", numeroRadicado);
        response.put("fecha_radicacion", solicitud.getFechaRadicacion());
        response.put("estado", solicitud.getEstado().name());

        return response;
    }

    /**
     * Obtiene todos los documentos guardados de un trámite
     */
    public Map<String, Object> obtenerDocumentos(Long tramiteId) {
        List<TramiteDocumento> documentos = documentoRepository.findBySolicitudId(tramiteId);

        List<Map<String, Object>> documentosInfo = documentos.stream()
                .map(doc -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", doc.getId());
                    info.put("documento_id", doc.getDocumentoId());
                    info.put("estado", doc.getEstado());
                    info.put("progreso", doc.getProgreso());
                    info.put("archivo_url", doc.getArchivoUrl());
                    info.put("fecha_actualizacion", doc.getFechaActualizacion());

                    // Parsear datos si existen
                    if (doc.getDatos() != null) {
                        try {
                            Map<String, Object> datos = objectMapper.readValue(
                                    doc.getDatos(),
                                    new TypeReference<Map<String, Object>>() {}
                            );
                            info.put("datos", datos);
                        } catch (IOException e) {
                            info.put("datos", null);
                        }
                    }

                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("tramite_id", tramiteId);
        response.put("documentos", documentosInfo);
        response.put("total", documentos.size());

        return response;
    }

    /**
     * Guarda un archivo en el sistema de archivos
     */
    private String guardarArchivo(Long tramiteId, String documentoId, MultipartFile archivo)
            throws IOException {

        // Crear directorio si no existe
        String tramiteDir = UPLOAD_DIR + "/" + tramiteId;
        Path dirPath = Paths.get(tramiteDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Generar nombre único para el archivo
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = documentoId + "_" +
                System.currentTimeMillis() + extension;

        // Guardar archivo
        Path filePath = dirPath.resolve(nombreArchivo);
        archivo.transferTo(filePath.toFile());

        // Retornar URL relativa
        return "/" + tramiteDir + "/" + nombreArchivo;
    }

    /**
     * Obtiene la extensión de un archivo
     */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }

    /**
     * Genera un número de radicado único
     */
    private String generarNumeroRadicado() {
        String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        String aleatorio = String.format("%06d", new Random().nextInt(999999));
        return "INV-" + fecha + "-" + aleatorio;
    }
}

