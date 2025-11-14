package com.segar.backend.documentos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.segar.backend.documentos.api.dto.DocumentTemplateDTO;
import com.segar.backend.documentos.domain.DocumentTemplate;
import com.segar.backend.documentos.infrastructure.DocumentTemplateRepository;
import com.segar.backend.security.service.AuthenticatedUserService;
import com.segar.backend.shared.domain.CategoriaRiesgo;
import com.segar.backend.shared.domain.TipoTramite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestión de plantillas de documentos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentTemplateServiceImpl {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final ObjectMapper objectMapper;
    private final AuthenticatedUserService authenticatedUserService;

     
    public List<DocumentTemplateDTO> getAllActiveTemplates() {
        log.info("Obteniendo todas las plantillas activas");
        return documentTemplateRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

     
    public DocumentTemplateDTO getTemplateById(Long id) {
        log.info("Obteniendo plantilla por ID: {}", id);
        DocumentTemplate template = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con ID: " + id));
        return convertToDTO(template);
    }

     
    public List<DocumentTemplateDTO> getTemplatesByTramite(TipoTramite tipoTramite) {
        log.info("Obteniendo plantillas para trámite: {}", tipoTramite);
        return documentTemplateRepository.findByTipoTramiteOrderedByDisplayOrder(tipoTramite)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

     
    public List<DocumentTemplateDTO> getTemplatesByTramiteAndRiesgo(TipoTramite tipoTramite, CategoriaRiesgo categoriaRiesgo) {
        log.info("Obteniendo plantillas para trámite: {} y categoría riesgo: {}", tipoTramite, categoriaRiesgo);
        List<DocumentTemplateDTO> templates = new ArrayList<DocumentTemplateDTO>();
        
        List<DocumentTemplateDTO> templatesI = documentTemplateRepository.findByTipoTramiteAndCategoriaRiesgo(tipoTramite, categoriaRiesgo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<DocumentTemplateDTO> templatesIIA = documentTemplateRepository.findByTipoTramiteAndCategoriaRiesgo(tipoTramite, CategoriaRiesgo.IIA)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<DocumentTemplateDTO> templatesIII = documentTemplateRepository.findByTipoTramiteAndCategoriaRiesgo(tipoTramite, CategoriaRiesgo.III)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        if (categoriaRiesgo == CategoriaRiesgo.I) {
            templates.addAll(templatesI);
        } else if (categoriaRiesgo == CategoriaRiesgo.IIA) {
            templates.addAll(templatesI);
            templates.addAll(templatesIIA);
        } else if (categoriaRiesgo == CategoriaRiesgo.III) {
            templates.addAll(templatesI);
            templates.addAll(templatesIIA);
            templates.addAll(templatesIII);
        }
        return templates;
    }

     
    public List<DocumentTemplateDTO> getRequiredTemplatesByTramite(TipoTramite tipoTramite) {
        log.info("Obteniendo plantillas obligatorias para trámite: {}", tipoTramite);
        return documentTemplateRepository.findRequiredByTipoTramite(tipoTramite)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las categorías únicas de documentos
     */
    public List<String> getAllCategories() {
        log.info("Obteniendo todas las categorías de documentos");
        return documentTemplateRepository.findByActiveTrue()
                .stream()
                .map(DocumentTemplate::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

     
    @Transactional
    public DocumentTemplateDTO createTemplate(DocumentTemplateDTO templateDTO) {
        log.info("Creando nueva plantilla: {}", templateDTO.getName());

        // Validaciones
        validateTemplateDTO(templateDTO);

        DocumentTemplate template = convertToEntity(templateDTO);
        template.setCreatedBy(authenticatedUserService.getCurrentUsername());

        DocumentTemplate savedTemplate = documentTemplateRepository.save(template);
        log.info("Plantilla creada exitosamente con ID: {}", savedTemplate.getId());

        return convertToDTO(savedTemplate);
    }

     
    @Transactional
    public DocumentTemplateDTO updateTemplate(Long id, DocumentTemplateDTO templateDTO) {
        log.info("Actualizando plantilla ID: {}", id);

        DocumentTemplate existingTemplate = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con ID: " + id));

        // Validaciones
        validateTemplateDTO(templateDTO);

        // Verificar si el código ha cambiado y no está duplicado
        if (!existingTemplate.getCode().equals(templateDTO.getCode()) &&
            documentTemplateRepository.existsByCodeAndIdNot(templateDTO.getCode(), id)) {
            throw new RuntimeException("Ya existe una plantilla con el código: " + templateDTO.getCode());
        }

        // Incrementar versión si hay cambios significativos
        if (hasSignificantChanges(existingTemplate, templateDTO)) {
            templateDTO.setVersion(existingTemplate.getVersion() + 1);
        }

        updateEntityFromDTO(existingTemplate, templateDTO);

        DocumentTemplate savedTemplate = documentTemplateRepository.save(existingTemplate);
        log.info("Plantilla actualizada exitosamente: {}", savedTemplate.getId());

        return convertToDTO(savedTemplate);
    }

     
    @Transactional
    public void deactivateTemplate(Long id) {
        log.info("Desactivando plantilla ID: {}", id);

        DocumentTemplate template = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con ID: " + id));

        template.setActive(false);
        documentTemplateRepository.save(template);

        log.info("Plantilla desactivada exitosamente: {}", id);
    }

     
    public boolean validateFieldsDefinition(String fieldsDefinition) {
        if (fieldsDefinition == null || fieldsDefinition.trim().isEmpty()) {
            return false;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(fieldsDefinition);

            // Validar que sea un array
            if (!jsonNode.isArray()) {
                return false;
            }

            // Validar cada campo
            for (JsonNode fieldNode : jsonNode) {
                if (!validateFieldDefinition(fieldNode)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error validando fieldsDefinition", e);
            return false;
        }
    }

     
    public boolean validateFileRules(String fileRules) {
        if (fileRules == null || fileRules.trim().isEmpty()) {
            return true; // Es opcional
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(fileRules);

            // Validar estructura básica
            return jsonNode.isObject();
        } catch (Exception e) {
            log.error("Error validando fileRules", e);
            return false;
        }
    }

    private DocumentTemplateDTO convertToDTO(DocumentTemplate template) {
        return DocumentTemplateDTO.builder()
                .id(template.getId())
                .code(template.getCode())
                .name(template.getName())
                .description(template.getDescription())
                .fieldsDefinition(template.getFieldsDefinition())
                .fileRules(template.getFileRules())
                .appliesToTramiteTypes(template.getAppliesToTramiteTypes())
                .version(template.getVersion())
                .active(template.getActive())
                .required(template.getRequired())
                .displayOrder(template.getDisplayOrder())
                .orden(template.getOrden() != null ? template.getOrden() : template.getDisplayOrder())
                .category(template.getCategory())
                .categoriaRiesgo(template.getCategoriaRiesgo())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .createdBy(template.getCreatedBy())
                .build();
    }

    private DocumentTemplate convertToEntity(DocumentTemplateDTO dto) {
        return DocumentTemplate.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .fieldsDefinition(dto.getFieldsDefinition())
                .fileRules(dto.getFileRules())
                .appliesToTramiteTypes(dto.getAppliesToTramiteTypes())
                .version(dto.getVersion() != null ? dto.getVersion() : 1)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .required(dto.getRequired() != null ? dto.getRequired() : false)
                .displayOrder(dto.getDisplayOrder())
                .categoriaRiesgo(dto.getCategoriaRiesgo())
                .createdBy(dto.getCreatedBy())
                .build();
    }

    private void updateEntityFromDTO(DocumentTemplate entity, DocumentTemplateDTO dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setFieldsDefinition(dto.getFieldsDefinition());
        entity.setFileRules(dto.getFileRules());
        entity.setAppliesToTramiteTypes(dto.getAppliesToTramiteTypes());
        entity.setVersion(dto.getVersion());
        entity.setActive(dto.getActive());
        entity.setRequired(dto.getRequired());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setOrden(dto.getOrden() != null ? dto.getOrden() : dto.getDisplayOrder());
        entity.setCategory(dto.getCategory());
        entity.setCategoriaRiesgo(dto.getCategoriaRiesgo());
    }

    private void validateTemplateDTO(DocumentTemplateDTO dto) {
        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de la plantilla es obligatorio");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la plantilla es obligatorio");
        }

        if (!validateFieldsDefinition(dto.getFieldsDefinition())) {
            throw new IllegalArgumentException("La definición de campos no es válida");
        }

        if (!validateFileRules(dto.getFileRules())) {
            throw new IllegalArgumentException("Las reglas de archivo no son válidas");
        }
    }

    private boolean validateFieldDefinition(JsonNode fieldNode) {
        // Validar campos obligatorios de un campo de formulario
        return fieldNode.has("key") &&
               fieldNode.has("label") &&
               fieldNode.has("type") &&
               fieldNode.get("key").isTextual() &&
               fieldNode.get("label").isTextual() &&
               fieldNode.get("type").isTextual();
    }

    private boolean hasSignificantChanges(DocumentTemplate existing, DocumentTemplateDTO updated) {
        // Cambios que requieren incrementar versión
        return !existing.getFieldsDefinition().equals(updated.getFieldsDefinition()) ||
               !existing.getFileRules().equals(updated.getFileRules()) ||
               !existing.getRequired().equals(updated.getRequired());
    }
}
