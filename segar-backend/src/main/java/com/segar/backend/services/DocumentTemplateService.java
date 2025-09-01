package com.segar.backend.services;

import com.segar.backend.dto.DocumentTemplateDTO;
import com.segar.backend.models.TipoTramite;
import com.segar.backend.models.CategoriaRiesgo;

import java.util.List;

/**
 * Servicio para gestión de plantillas de documentos
 */
public interface DocumentTemplateService {

    /**
     * Obtiene todas las plantillas activas
     */
    List<DocumentTemplateDTO> getAllActiveTemplates();

    /**
     * Obtiene una plantilla por ID
     */
    DocumentTemplateDTO getTemplateById(Long id);

    /**
     * Obtiene plantillas aplicables a un tipo de trámite
     */
    List<DocumentTemplateDTO> getTemplatesByTramite(TipoTramite tipoTramite);

    /**
     * Obtiene plantillas por trámite y categoría de riesgo
     */
    List<DocumentTemplateDTO> getTemplatesByTramiteAndRiesgo(TipoTramite tipoTramite, CategoriaRiesgo categoriaRiesgo);

    /**
     * Obtiene plantillas obligatorias para un trámite
     */
    List<DocumentTemplateDTO> getRequiredTemplatesByTramite(TipoTramite tipoTramite);

    /**
     * Crea una nueva plantilla
     */
    DocumentTemplateDTO createTemplate(DocumentTemplateDTO templateDTO);

    /**
     * Actualiza una plantilla existente
     */
    DocumentTemplateDTO updateTemplate(Long id, DocumentTemplateDTO templateDTO);

    /**
     * Desactiva una plantilla
     */
    void deactivateTemplate(Long id);

    /**
     * Valida la estructura de fieldsDefinition JSON
     */
    boolean validateFieldsDefinition(String fieldsDefinition);

    /**
     * Valida las reglas de archivo JSON
     */
    boolean validateFileRules(String fileRules);
}
