package com.segar.backend.documentos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.segar.backend.dto.DocumentExportRequestDTO;
import com.segar.backend.dto.DocumentExportResponseDTO;
import com.segar.backend.models.DocumentInstance;
import com.segar.backend.models.DocumentTemplate;
import com.segar.backend.repositories.DocumentInstanceRepository;
import com.segar.backend.services.DocumentPdfService;
import com.segar.backend.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de generación de PDFs usando Thymeleaf + OpenHTMLToPDF
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThymeleafDocumentPdfService {

    private final TemplateEngine templateEngine;
    private final FileStorageService fileStorageService;
    private final DocumentInstanceRepository documentInstanceRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DocumentExportResponseDTO generatePdf(DocumentInstance instance, DocumentExportRequestDTO request) throws Exception {
        log.info("Generando PDF para documento instancia ID: {}", instance.getId());

        // Preparar datos para el template
        Context context = prepareTemplateContext(instance);

        // Seleccionar template HTML basado en el tipo de documento
        String templateName = getTemplateName(instance.getTemplate());

        // Procesar template HTML
        String htmlContent = templateEngine.process(templateName, context);

        // Generar PDF desde HTML
        byte[] pdfBytes = generatePdfFromHtml(htmlContent);

        // Almacenar PDF
        String fileName = generateFileName(instance);
        String storageKey = fileStorageService.store(
            new ByteArrayInputStream(pdfBytes),
            fileName,
            "documents/pdf",
            "application/pdf"
        );

        // Actualizar instancia con información del PDF
        instance.setFileUrl(fileStorageService.getPublicUrl(storageKey));
        instance.setStorageKey(storageKey);
        instance.setFileMime("application/pdf");
        instance.setFileSize((long) pdfBytes.length);
        instance.setStatus(DocumentInstance.DocumentStatus.FINALIZED);

        documentInstanceRepository.save(instance);

        return DocumentExportResponseDTO.builder()
            .fileUrl(instance.getFileUrl())
            .fileSize(instance.getFileSize())
            .fileMime("application/pdf")
            .storageKey(storageKey)
            .suggestedFileName(fileName)
            .status("SUCCESS")
            .message("PDF generado exitosamente")
            .build();
    }

    @Override
    public DocumentExportResponseDTO generateCombinedPdf(Long tramiteId, DocumentExportRequestDTO request) throws Exception {
        log.info("Generando PDF combinado para trámite ID: {}", tramiteId);

        List<DocumentInstance> instances = documentInstanceRepository.findByTramiteId(tramiteId);

        if (instances.isEmpty()) {
            throw new IllegalStateException("No hay documentos para generar PDF combinado");
        }

        // Preparar contexto combinado
        Context context = prepareCombinedTemplateContext(instances);

        // Procesar template combinado
        String htmlContent = templateEngine.process("pdf/combined-documents", context);

        // Generar PDF desde HTML
        byte[] pdfBytes = generatePdfFromHtml(htmlContent);

        // Almacenar PDF combinado
        String fileName = "tramite_" + tramiteId + "_documentos_" +
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        String storageKey = fileStorageService.store(
            new ByteArrayInputStream(pdfBytes),
            fileName,
            "documents/combined",
            "application/pdf"
        );

        return DocumentExportResponseDTO.builder()
            .fileUrl(fileStorageService.getPublicUrl(storageKey))
            .fileSize((long) pdfBytes.length)
            .fileMime("application/pdf")
            .storageKey(storageKey)
            .suggestedFileName(fileName)
            .status("SUCCESS")
            .message("PDF combinado generado exitosamente")
            .build();
    }

    @Override
    public boolean isAvailable() {
        try {
            // Test básico para verificar que las librerías están disponibles
            new PdfRendererBuilder();
            return true;
        } catch (Exception e) {
            log.error("Servicio PDF no disponible", e);
            return false;
        }
    }

    private Context prepareTemplateContext(DocumentInstance instance) throws Exception {
        Context context = new Context();

        // Datos básicos del documento
        context.setVariable("documento", instance);
        context.setVariable("plantilla", instance.getTemplate());
        context.setVariable("tramite", instance.getTramite());

        // Datos del formulario rellenado
        if (instance.getFilledData() != null) {
            Map<String, Object> formData = objectMapper.readValue(instance.getFilledData(), Map.class);
            context.setVariable("datosFormulario", formData);
        }

        // Metadatos adicionales
        if (instance.getMetadata() != null) {
            Map<String, Object> metadata = objectMapper.readValue(instance.getMetadata(), Map.class);
            context.setVariable("metadata", metadata);
        }

        // Información de generación
        context.setVariable("fechaGeneracion", LocalDateTime.now());
        context.setVariable("formatoFecha", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return context;
    }

    private Context prepareCombinedTemplateContext(List<DocumentInstance> instances) throws Exception {
        Context context = new Context();

        // Lista de documentos
        context.setVariable("documentos", instances);

        // Información del trámite (tomamos del primer documento)
        if (!instances.isEmpty()) {
            context.setVariable("tramite", instances.get(0).getTramite());
            context.setVariable("empresaId", instances.get(0).getEmpresaId());
        }

        // Información de generación
        context.setVariable("fechaGeneracion", LocalDateTime.now());
        context.setVariable("formatoFecha", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return context;
    }

    private String getTemplateName(DocumentTemplate template) {
        // Mapear códigos de plantilla a templates HTML específicos
        return switch (template.getCode()) {
            case "FICHA_TECNICA" -> "pdf/ficha-tecnica";
            case "ETIQUETA" -> "pdf/etiqueta";
            case "CERTIFICADO_ANALISIS" -> "pdf/certificado-analisis";
            case "CERTIFICADO_BPM" -> "pdf/certificado-bpm";
            case "FORMULARIO_SOLICITUD" -> "pdf/formulario-solicitud";
            default -> "pdf/documento-generico";
        };
    }

    private byte[] generatePdfFromHtml(String htmlContent) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        }
    }

    private String generateFileName(DocumentInstance instance) {
        String templateCode = instance.getTemplate().getCode().toLowerCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s_%s.pdf", templateCode, instance.getId(), timestamp);
    }
}
