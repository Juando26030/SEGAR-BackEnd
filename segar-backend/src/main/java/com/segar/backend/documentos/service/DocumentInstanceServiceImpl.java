package com.segar.backend.documentos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.segar.backend.documentos.domain.*;
import com.segar.backend.documentos.infrastructure.*;
import com.segar.backend.documentos.api.dto.*;
import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.shared.domain.Tramite;
import com.segar.backend.shared.infrastructure.TramiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestión de instancias de documentos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentInstanceServiceImpl {

    private final DocumentInstanceRepository documentInstanceRepository;
    private final DocumentTemplateRepository documentTemplateRepository;
    private final DocumentTemplateServiceImpl documentTemplateService;
    private final LocalFileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final ThymeleafDocumentPdfService documentPdfService;


    private final TramiteRepository tramiteRepository;


     
    public List<DocumentInstanceDTO> getInstancesByTramite(Long tramiteId) {
        log.info("Obteniendo instancias de documentos para trámite: {}", tramiteId);

        List<DocumentInstance> instances = documentInstanceRepository.findByTramiteId(tramiteId);
        return instances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

     
    public DocumentInstanceDTO getInstanceById(Long id) {
        log.info("Obteniendo instancia de documento por ID: {}", id);

        DocumentInstance instance = documentInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instancia de documento no encontrada con ID: " + id));

        return convertToDTO(instance);
    }

     
    @Transactional
    public DocumentInstanceDTO createInstance(Long tramiteId, DocumentInstanceRequestDTO requestDTO) {
        log.info("Creando instancia de documento para trámite: {}, plantilla: {}", tramiteId, requestDTO.getTemplateId());

        // Verificar que el trámite existe
        Tramite tramite = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado con ID: " + tramiteId));

        // Verificar que la plantilla existe
        DocumentTemplate template = documentTemplateRepository.findById(requestDTO.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con ID: " + requestDTO.getTemplateId()));

        // Verificar si ya existe una instancia para esta plantilla y trámite
        Optional<DocumentInstance> existingInstance = documentInstanceRepository
                .findByTramiteIdAndTemplate(tramiteId, template);

        if (existingInstance.isPresent()) {
            throw new RuntimeException("Ya existe una instancia de documento para esta plantilla y trámite");
        }

        // Validar datos del formulario si se proporcionan
        if (requestDTO.getFilledData() != null && !requestDTO.getFilledData().isEmpty()) {
            String filledDataJson = convertMapToJson(requestDTO.getFilledData());
            if (!validateInstanceData(template.getId(), filledDataJson)) {
                throw new RuntimeException("Los datos del formulario no son válidos según la plantilla");
            }
        }

        // Crear nueva instancia
        DocumentInstance instance = DocumentInstance.builder()
                .template(template)
                .tramite(tramite)
                .empresaId(1L) // TODO: Obtener empresaId del contexto de usuario autenticado
                .status(DocumentInstance.DocumentStatus.DRAFT)
                .filledData(requestDTO.getFilledData() != null ? convertMapToJson(requestDTO.getFilledData()) : null)
                .metadata(requestDTO.getMetadata() != null ? convertMapToJson(requestDTO.getMetadata()) : null)
                .createdBy("SYSTEM") // TODO: Obtener del contexto de seguridad
                .build();

        DocumentInstance savedInstance = documentInstanceRepository.save(instance);
        log.info("Instancia de documento creada exitosamente con ID: {}", savedInstance.getId());

        return convertToDTO(savedInstance);
    }

     
    @Transactional
    public DocumentInstanceDTO updateInstance(Long tramiteId, Long instanceId, DocumentInstanceRequestDTO requestDTO) {
        log.info("Actualizando instancia de documento: {} del trámite: {}", instanceId, tramiteId);

        DocumentInstance instance = findInstanceByTramiteAndId(tramiteId, instanceId);

        // Validar datos del formulario si se proporcionan
        if (requestDTO.getFilledData() != null && !requestDTO.getFilledData().isEmpty()) {
            String filledDataJson = convertMapToJson(requestDTO.getFilledData());
            if (!validateInstanceData(instance.getTemplate().getId(), filledDataJson)) {
                throw new RuntimeException("Los datos del formulario no son válidos según la plantilla");
            }
            instance.setFilledData(filledDataJson);
            instance.setStatus(DocumentInstance.DocumentStatus.FILLED);
        }

        // Actualizar metadatos si se proporcionan
        if (requestDTO.getMetadata() != null) {
            instance.setMetadata(convertMapToJson(requestDTO.getMetadata()));
        }

        DocumentInstance savedInstance = documentInstanceRepository.save(instance);
        log.info("Instancia de documento actualizada exitosamente: {}", savedInstance.getId());

        return convertToDTO(savedInstance);
    }

     
    @Transactional
    public DocumentInstanceDTO uploadFiles(Long tramiteId, Long instanceId, MultipartFile[] files) {
        log.info("Subiendo archivos para instancia: {} del trámite: {}", instanceId, tramiteId);

        DocumentInstance instance = findInstanceByTramiteAndId(tramiteId, instanceId);

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No se proporcionaron archivos para subir");
        }

        try {
            // Validar archivos según las reglas de la plantilla
            validateUploadedFiles(instance.getTemplate(), files);

            // Por simplicidad, tomamos el primer archivo (se puede extender para múltiples)
            MultipartFile file = files[0];
            String folder = "documents/" + tramiteId + "/" + instanceId;
            String storageKey = fileStorageService.store(file, folder);

            // Actualizar instancia con información del archivo
            instance.setStorageKey(storageKey);
            instance.setFileUrl(fileStorageService.getPublicUrl(storageKey));
            instance.setFileMime(file.getContentType());
            instance.setFileSize(file.getSize());
            instance.setStatus(DocumentInstance.DocumentStatus.UPLOADED);

            DocumentInstance savedInstance = documentInstanceRepository.save(instance);
            log.info("Archivos subidos exitosamente para instancia: {}", savedInstance.getId());

            return convertToDTO(savedInstance);

        } catch (Exception e) {
            log.error("Error subiendo archivos para instancia: {}", instanceId, e);
            throw new RuntimeException("Error subiendo archivos: " + e.getMessage());
        }
    }

     
    @Transactional
    public DocumentExportResponseDTO exportToPdf(Long tramiteId, Long instanceId, DocumentExportRequestDTO requestDTO) {
        log.info("Exportando a PDF instancia: {} del trámite: {}", instanceId, tramiteId);

        DocumentInstance instance = findInstanceByTramiteAndId(tramiteId, instanceId);

        // Verificar que la instancia tenga datos suficientes para exportar
        if (instance.getFilledData() == null || instance.getFilledData().trim().isEmpty()) {
            throw new RuntimeException("La instancia no tiene datos suficientes para exportar a PDF");
        }

        try {
            DocumentExportResponseDTO response = documentPdfService.generatePdf(instance, requestDTO);
            log.info("PDF exportado exitosamente para instancia: {}", instanceId);
            return response;

        } catch (Exception e) {
            log.error("Error exportando PDF para instancia: {}", instanceId, e);
            throw new RuntimeException("Error exportando PDF: " + e.getMessage());
        }
    }

     
    @Transactional
    public void deleteInstance(Long tramiteId, Long instanceId) {
        log.info("Eliminando instancia: {} del trámite: {}", instanceId, tramiteId);

        DocumentInstance instance = findInstanceByTramiteAndId(tramiteId, instanceId);

        // Eliminar archivo asociado si existe
        if (instance.getStorageKey() != null) {
            try {
                fileStorageService.delete(instance.getStorageKey());
            } catch (Exception e) {
                log.warn("Error eliminando archivo asociado a instancia: {}", instanceId, e);
            }
        }

        documentInstanceRepository.delete(instance);
        log.info("Instancia eliminada exitosamente: {}", instanceId);
    }

     
    public boolean areAllRequiredDocumentsCompleted(Long tramiteId) {
        log.info("Verificando completitud de documentos obligatorios para trámite: {}", tramiteId);

        long requiredCompleted = documentInstanceRepository.countRequiredFinalizedByTramiteId(tramiteId);
        long requiredTotal = documentInstanceRepository.countRequiredTemplatesByTramiteId(tramiteId);

        return requiredCompleted >= requiredTotal;
    }

     
    public DocumentCompletionSummaryDTO getDocumentCompletionSummary(Long tramiteId) {
        log.info("Obteniendo resumen de completitud para trámite: {}", tramiteId);

        // Obtener trámite para conocer el tipo
        Tramite tramite = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado con ID: " + tramiteId));

        // Obtener plantillas requeridas y opcionales
        TipoTramite tipoTramite = convertProcedureTypeToTipoTramite(tramite.getProcedureType());
        List<DocumentTemplateDTO> requiredTemplates = documentTemplateService
                .getRequiredTemplatesByTramite(tipoTramite);
        List<DocumentTemplateDTO> allTemplates = documentTemplateService
                .getTemplatesByTramite(tipoTramite);

        // Obtener instancias existentes
        List<DocumentInstance> instances = documentInstanceRepository.findByTramiteId(tramiteId);
        Set<Long> completedTemplateIds = instances.stream()
                .filter(i -> i.getStatus() == DocumentInstance.DocumentStatus.FINALIZED)
                .map(i -> i.getTemplate().getId())
                .collect(Collectors.toSet());

        // Calcular estadísticas
        int totalRequired = requiredTemplates.size();
        int completedRequired = (int) requiredTemplates.stream()
                .mapToLong(DocumentTemplateDTO::getId)
                .filter(completedTemplateIds::contains)
                .count();

        List<DocumentTemplateDTO> optionalTemplates = allTemplates.stream()
                .filter(t -> !t.getRequired())
                .collect(Collectors.toList());

        int totalOptional = optionalTemplates.size();
        int completedOptional = (int) optionalTemplates.stream()
                .mapToLong(DocumentTemplateDTO::getId)
                .filter(completedTemplateIds::contains)
                .count();

        // Documentos faltantes
        List<DocumentTemplateDTO> missingRequired = requiredTemplates.stream()
                .filter(t -> !completedTemplateIds.contains(t.getId()))
                .collect(Collectors.toList());

        double completionPercentage = totalRequired > 0 ?
                (double) completedRequired / totalRequired * 100 : 100;

        return DocumentCompletionSummaryDTO.builder()
                .tramiteId(tramiteId)
                .totalRequired(totalRequired)
                .completedRequired(completedRequired)
                .totalOptional(totalOptional)
                .completedOptional(completedOptional)
                .completionPercentage(completionPercentage)
                .allRequiredCompleted(completedRequired >= totalRequired)
                .missingRequired(missingRequired)
                .availableOptional(optionalTemplates.stream()
                        .filter(t -> !completedTemplateIds.contains(t.getId()))
                        .collect(Collectors.toList()))
                .build();
    }

     
    public boolean validateInstanceData(Long templateId, String filledData) {
        if (filledData == null || filledData.trim().isEmpty()) {
            return true; // Datos vacíos son válidos (borrador)
        }

        try {
            DocumentTemplate template = documentTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));

            return documentTemplateService.validateFieldsDefinition(template.getFieldsDefinition()) &&
                   isValidAgainstSchema(filledData, template.getFieldsDefinition());

        } catch (Exception e) {
            log.error("Error validando datos de instancia", e);
            return false;
        }
    }

    // Métodos auxiliares privados
    private DocumentInstanceDTO convertToDTO(DocumentInstance instance) {
        return DocumentInstanceDTO.builder()
                .id(instance.getId())
                .templateId(instance.getTemplate().getId())
                .templateCode(instance.getTemplate().getCode())
                .templateName(instance.getTemplate().getName())
                .tramiteId(instance.getTramite() != null ? instance.getTramite().getId() : null)
                .empresaId(instance.getEmpresaId())
                .status(instance.getStatus())
                .filledData(instance.getFilledData())
                .fileUrl(instance.getFileUrl())
                .fileMime(instance.getFileMime())
                .fileSize(instance.getFileSize())
                .storageKey(instance.getStorageKey())
                .metadata(instance.getMetadata())
                .version(instance.getVersion())
                .createdAt(instance.getCreatedAt())
                .updatedAt(instance.getUpdatedAt())
                .createdBy(instance.getCreatedBy())
                .isRequired(instance.getTemplate().getRequired())
                .templateDescription(instance.getTemplate().getDescription())
                .fieldsDefinition(instance.getTemplate().getFieldsDefinition())
                .fileRules(instance.getTemplate().getFileRules())
                .build();
    }

    private DocumentInstance findInstanceByTramiteAndId(Long tramiteId, Long instanceId) {
        DocumentInstance instance = documentInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada con ID: " + instanceId));

        if (instance.getTramite() == null || !instance.getTramite().getId().equals(tramiteId)) {
            throw new RuntimeException("La instancia no pertenece al trámite especificado");
        }

        return instance;
    }

    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo mapa a JSON", e);
        }
    }

    private void validateUploadedFiles(DocumentTemplate template, MultipartFile[] files) {
        if (template.getFileRules() == null) {
            return; // No hay reglas específicas
        }

        try {
            JsonNode rules = objectMapper.readTree(template.getFileRules());

            for (MultipartFile file : files) {
                validateSingleFile(file, rules);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error validando archivos: " + e.getMessage());
        }
    }

    private void validateSingleFile(MultipartFile file, JsonNode rules) {
        // Validar tamaño máximo
        if (rules.has("maxSize") && file.getSize() > rules.get("maxSize").asLong()) {
            throw new RuntimeException("Archivo excede el tamaño máximo permitido");
        }

        // Validar tipo MIME
        if (rules.has("allowedMime")) {
            JsonNode allowedTypes = rules.get("allowedMime");
            boolean validType = false;

            if (allowedTypes.isArray()) {
                for (JsonNode typeNode : allowedTypes) {
                    if (typeNode.asText().equals(file.getContentType())) {
                        validType = true;
                        break;
                    }
                }
            }

            if (!validType) {
                throw new RuntimeException("Tipo de archivo no permitido: " + file.getContentType());
            }
        }
    }

    private boolean isValidAgainstSchema(String filledData, String fieldsDefinition) {
        // Implementación básica de validación contra esquema
        // Se puede extender con librerías más robustas como JSON Schema Validator
        try {
            JsonNode data = objectMapper.readTree(filledData);
            JsonNode schema = objectMapper.readTree(fieldsDefinition);

            // Validación básica: verificar campos requeridos
            if (schema.isArray()) {
                for (JsonNode fieldDef : schema) {
                    if (fieldDef.has("required") && fieldDef.get("required").asBoolean()) {
                        String fieldKey = fieldDef.get("key").asText();
                        if (!data.has(fieldKey) || data.get(fieldKey).isNull()) {
                            log.warn("Campo requerido faltante: {}", fieldKey);
                            return false;
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error validando datos contra esquema", e);
            return false;
        }
    }

    /**
     * Convierte el procedureType de la entidad Tramite al enum TipoTramite
     */
    private TipoTramite convertProcedureTypeToTipoTramite(String procedureType) {
        if (procedureType == null) {
            return TipoTramite.REGISTRO; // Default
        }

        return switch (procedureType.toUpperCase()) {
            case "REGISTRO", "REGISTRO_SANITARIO" -> TipoTramite.REGISTRO;
            case "RENOVACION", "RENOVACION_SANITARIA" -> TipoTramite.RENOVACION;
            case "MODIFICACION", "MODIFICACION_SANITARIA" -> TipoTramite.MODIFICACION;
            default -> {
                log.warn("Tipo de procedimiento desconocido: {}. Usando REGISTRO por defecto", procedureType);
                yield TipoTramite.REGISTRO;
            }
        };
    }
}
