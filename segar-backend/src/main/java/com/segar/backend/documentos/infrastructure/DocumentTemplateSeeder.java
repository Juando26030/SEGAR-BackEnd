package com.segar.backend.documentos.infrastructure;

import com.segar.backend.documentos.domain.DocumentTemplate;
import com.segar.backend.shared.domain.CategoriaRiesgo;
import com.segar.backend.shared.domain.TipoTramite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Set;

/**
 * Seeder para pre-cargar plantillas de documentos según normativa INVIMA
 * Basado en requerimientos del frontend para NSO, PSA y RSA
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocumentTemplateSeeder {

    private final DocumentTemplateRepository documentTemplateRepository;

    @Bean
    @Order(1)
    public CommandLineRunner seedDocumentTemplates() {
        return args -> {
            if (documentTemplateRepository.count() > 0) {
                log.info("Las plantillas de documentos ya existen. Omitiendo seeder.");
                return;
            }

            log.info("Iniciando carga de plantillas de documentos INVIMA...");

            // ========== DOCUMENTOS BÁSICOS (NSO - Riesgo BAJO) ==========
            createTemplate(
                "CERTIFICADO_EXISTENCIA",
                "Certificado de Existencia y Representación Legal",
                "Certificado expedido por la Cámara de Comercio con vigencia no mayor a 30 días",
                "basico",
                CategoriaRiesgo.I,
                true,
                1,
                buildFieldsDefinitionCertificadoExistencia(),
                buildFileRulesPDF()
            );

            createTemplate(
                "RUT_EMPRESA",
                "RUT de la Empresa",
                "Registro Único Tributario actualizado de la empresa fabricante o importadora",
                "basico",
                CategoriaRiesgo.I,
                true,
                2,
                buildFieldsDefinitionRUT(),
                buildFileRulesPDF()
            );

            createTemplate(
                "FICHA_TECNICA_BASICA",
                "Ficha Técnica Básica del Producto",
                "Información técnica básica del producto alimentario",
                "basico",
                CategoriaRiesgo.I,
                true,
                3,
                buildFieldsDefinitionFichaTecnicaBasica(),
                buildFileRulesMultiple()
            );

            createTemplate(
                "ETIQUETA_PRODUCTO",
                "Etiqueta del Producto",
                "Diseño de etiqueta cumpliendo con la Resolución 5109 de 2005",
                "basico",
                CategoriaRiesgo.I,
                true,
                4,
                buildFieldsDefinitionEtiqueta(),
                buildFileRulesImages()
            );

            createTemplate(
                "CONCEPTO_SANITARIO",
                "Concepto Sanitario Favorable",
                "Concepto sanitario de la planta de producción o certificado de inspección sanitaria",
                "certificacion",
                CategoriaRiesgo.I,
                true,
                5,
                buildFieldsDefinitionConceptoSanitario(),
                buildFileRulesPDF()
            );

            // ========== DOCUMENTOS ADICIONALES PSA (Riesgo MEDIO) ==========
            createTemplate(
                "ANALISIS_MICROBIOLOGICO",
                "Análisis Microbiológico",
                "Resultados de análisis microbiológico del producto realizado por laboratorio acreditado",
                "analisis",
                CategoriaRiesgo.IIA,
                true,
                6,
                buildFieldsDefinitionAnalisisMicrobiologico(),
                buildFileRulesPDF()
            );

            createTemplate(
                "ANALISIS_FISICOQUIMICO",
                "Análisis Fisicoquímico",
                "Resultados de análisis fisicoquímico del producto",
                "analisis",
                CategoriaRiesgo.IIA,
                true,
                7,
                buildFieldsDefinitionAnalisisFisicoquimico(),
                buildFileRulesPDF()
            );

            createTemplate(
                "CERTIFICADO_BPM",
                "Certificado de Buenas Prácticas de Manufactura",
                "Certificado vigente de BPM expedido por autoridad sanitaria competente",
                "certificacion",
                CategoriaRiesgo.IIA,
                true,
                8,
                buildFieldsDefinitionCertificadoBPM(),
                buildFileRulesPDF()
            );

            createTemplate(
                "FICHA_TECNICA_MATERIAS_PRIMAS",
                "Ficha Técnica de Materias Primas",
                "Especificaciones técnicas de las materias primas utilizadas",
                "basico",
                CategoriaRiesgo.IIA,
                false,
                9,
                buildFieldsDefinitionMateriasPrimas(),
                buildFileRulesMultiple()
            );

            createTemplate(
                "DIAGRAMA_FLUJO",
                "Diagrama de Flujo de Producción",
                "Diagrama del proceso de fabricación del producto",
                "otros",
                CategoriaRiesgo.IIA,
                false,
                10,
                buildFieldsDefinitionDiagramaFlujo(),
                buildFileRulesImages()
            );

            // ========== DOCUMENTOS ADICIONALES RSA (Riesgo ALTO) ==========
            createTemplate(
                "PLAN_HACCP",
                "Plan HACCP",
                "Plan de Análisis de Peligros y Puntos Críticos de Control",
                "certificacion",
                CategoriaRiesgo.III,
                true,
                11,
                buildFieldsDefinitionPlanHACCP(),
                buildFileRulesPDF()
            );

            createTemplate(
                "ESTUDIO_ESTABILIDAD",
                "Estudio de Estabilidad",
                "Estudio que demuestre la vida útil del producto bajo condiciones de almacenamiento",
                "estudios",
                CategoriaRiesgo.III,
                true,
                12,
                buildFieldsDefinitionEstudioEstabilidad(),
                buildFileRulesMultiple()
            );

            createTemplate(
                "CERTIFICADO_CALIDAD_PROVEEDOR",
                "Certificados de Calidad de Proveedores",
                "Certificados de calidad de materias primas e insumos críticos",
                "certificacion",
                CategoriaRiesgo.III,
                false,
                13,
                buildFieldsDefinitionCertificadosProveedores(),
                buildFileRulesMultiple()
            );

            createTemplate(
                "MANUAL_CALIDAD",
                "Manual de Calidad",
                "Manual de aseguramiento de calidad de la empresa",
                "certificacion",
                CategoriaRiesgo.III,
                false,
                14,
                buildFieldsDefinitionManualCalidad(),
                buildFileRulesPDF()
            );

            createTemplate(
                "ESTUDIOS_VALIDACION",
                "Estudios de Validación de Procesos",
                "Validación de procesos críticos de manufactura",
                "estudios",
                CategoriaRiesgo.III,
                false,
                15,
                buildFieldsDefinitionEstudiosValidacion(),
                buildFileRulesMultiple()
            );

            createTemplate(
                "ANALISIS_NUTRICIONAL",
                "Análisis Nutricional Completo",
                "Tabla nutricional completa del producto según Resolución 333 de 2011",
                "analisis",
                CategoriaRiesgo.III,
                true,
                16,
                buildFieldsDefinitionAnalisisNutricional(),
                buildFileRulesPDF()
            );

            log.info("✅ Plantillas de documentos cargadas exitosamente: {} registros", documentTemplateRepository.count());
        };
    }

    private void createTemplate(String code, String name, String description, String category,
                                CategoriaRiesgo riesgo, boolean required, int orden,
                                String fieldsDefinition, String fileRules) {
        DocumentTemplate template = DocumentTemplate.builder()
                .code(code)
                .name(name)
                .description(description)
                .category(category)
                .categoriaRiesgo(riesgo)
                .required(required)
                .orden(orden)
                .displayOrder(orden)
                .fieldsDefinition(fieldsDefinition)
                .fileRules(fileRules)
                .appliesToTramiteTypes(Set.of(TipoTramite.REGISTRO))
                .version(1)
                .active(true)
                .createdBy("SYSTEM")
                .build();

        documentTemplateRepository.save(template);
        log.debug("✓ Plantilla creada: {}", name);
    }

    // ========== DEFINICIONES DE CAMPOS JSON ==========

    private String buildFieldsDefinitionCertificadoExistencia() {
        return """
            [
              {
                "nombre": "razon_social",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Razón social de la empresa",
                "descripcion": "Razón social tal como aparece en el certificado"
              },
              {
                "nombre": "nit",
                "tipo": "text",
                "requerido": true,
                "placeholder": "NIT sin dígito de verificación",
                "validacion": {
                  "pattern": "^[0-9]{9,10}$",
                  "mensaje": "NIT debe tener 9 o 10 dígitos"
                }
              },
              {
                "nombre": "fecha_expedicion",
                "tipo": "date",
                "requerido": true,
                "descripcion": "Fecha de expedición del certificado (máximo 30 días)"
              },
              {
                "nombre": "representante_legal",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Nombre completo del representante legal"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionRUT() {
        return """
            [
              {
                "nombre": "nit_empresa",
                "tipo": "text",
                "requerido": true,
                "placeholder": "NIT de la empresa"
              },
              {
                "nombre": "actividad_economica",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Código CIIU de actividad económica"
              },
              {
                "nombre": "fecha_actualizacion",
                "tipo": "date",
                "requerido": true,
                "descripcion": "Fecha de última actualización del RUT"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionFichaTecnicaBasica() {
        return """
            [
              {
                "nombre": "nombre_comercial",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Ej: Galletas Integrales Premium",
                "validacion": {
                  "min": 3,
                  "max": 100
                }
              },
              {
                "nombre": "descripcion_producto",
                "tipo": "textarea",
                "requerido": true,
                "placeholder": "Descripción detallada del producto",
                "validacion": {
                  "min": 20,
                  "max": 500
                }
              },
              {
                "nombre": "vida_util",
                "tipo": "number",
                "requerido": true,
                "placeholder": "Vida útil en meses",
                "validacion": {
                  "min": 1,
                  "max": 120
                }
              },
              {
                "nombre": "condiciones_almacenamiento",
                "tipo": "select",
                "requerido": true,
                "opciones": [
                  {"valor": "ambiente", "etiqueta": "Temperatura ambiente"},
                  {"valor": "refrigerado", "etiqueta": "Refrigerado (2-8°C)"},
                  {"valor": "congelado", "etiqueta": "Congelado (-18°C o menos)"}
                ]
              },
              {
                "nombre": "presentaciones",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Ej: 250g, 500g, 1kg"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionEtiqueta() {
        return """
            [
              {
                "nombre": "marca",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Marca del producto"
              },
              {
                "nombre": "contenido_neto",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Ej: 500g, 1L"
              },
              {
                "nombre": "lista_ingredientes",
                "tipo": "textarea",
                "requerido": true,
                "descripcion": "Lista de ingredientes en orden descendente de peso"
              },
              {
                "nombre": "registro_sanitario",
                "tipo": "text",
                "requerido": false,
                "placeholder": "Número de registro (si ya existe)"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionConceptoSanitario() {
        return """
            [
              {
                "nombre": "numero_concepto",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Número del concepto sanitario"
              },
              {
                "nombre": "entidad_emisora",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Entidad que expide el concepto"
              },
              {
                "nombre": "fecha_expedicion",
                "tipo": "date",
                "requerido": true
              },
              {
                "nombre": "vigencia",
                "tipo": "date",
                "requerido": true,
                "descripcion": "Fecha de vencimiento del concepto"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionAnalisisMicrobiologico() {
        return """
            [
              {
                "nombre": "laboratorio",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Nombre del laboratorio acreditado"
              },
              {
                "nombre": "numero_informe",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Número del informe de ensayo"
              },
              {
                "nombre": "fecha_analisis",
                "tipo": "date",
                "requerido": true
              },
              {
                "nombre": "parametros_analizados",
                "tipo": "textarea",
                "requerido": true,
                "descripcion": "Parámetros microbiológicos evaluados"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionAnalisisFisicoquimico() {
        return """
            [
              {
                "nombre": "laboratorio",
                "tipo": "text",
                "requerido": true
              },
              {
                "nombre": "fecha_analisis",
                "tipo": "date",
                "requerido": true
              },
              {
                "nombre": "ph",
                "tipo": "number",
                "requerido": false,
                "placeholder": "Valor de pH"
              },
              {
                "nombre": "humedad",
                "tipo": "number",
                "requerido": false,
                "placeholder": "% Humedad"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionCertificadoBPM() {
        return """
            [
              {
                "nombre": "numero_certificado",
                "tipo": "text",
                "requerido": true
              },
              {
                "nombre": "autoridad_sanitaria",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Autoridad que expide el certificado"
              },
              {
                "nombre": "fecha_emision",
                "tipo": "date",
                "requerido": true
              },
              {
                "nombre": "vigencia",
                "tipo": "date",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionMateriasPrimas() {
        return """
            [
              {
                "nombre": "materia_prima",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Nombre de la materia prima"
              },
              {
                "nombre": "proveedor",
                "tipo": "text",
                "requerido": true
              },
              {
                "nombre": "especificaciones",
                "tipo": "textarea",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionDiagramaFlujo() {
        return """
            [
              {
                "nombre": "tipo_diagrama",
                "tipo": "select",
                "requerido": true,
                "opciones": [
                  {"valor": "bloques", "etiqueta": "Diagrama de bloques"},
                  {"valor": "flujo", "etiqueta": "Diagrama de flujo"}
                ]
              },
              {
                "nombre": "descripcion_proceso",
                "tipo": "textarea",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionPlanHACCP() {
        return """
            [
              {
                "nombre": "fecha_elaboracion",
                "tipo": "date",
                "requerido": true
              },
              {
                "nombre": "responsable",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Responsable del plan HACCP"
              },
              {
                "nombre": "puntos_criticos_identificados",
                "tipo": "number",
                "requerido": true,
                "placeholder": "Número de PCC identificados"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionEstudioEstabilidad() {
        return """
            [
              {
                "nombre": "duracion_estudio",
                "tipo": "number",
                "requerido": true,
                "placeholder": "Duración en meses"
              },
              {
                "nombre": "condiciones_ensayo",
                "tipo": "textarea",
                "requerido": true,
                "descripcion": "Condiciones bajo las cuales se realizó el estudio"
              },
              {
                "nombre": "conclusiones",
                "tipo": "textarea",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionCertificadosProveedores() {
        return """
            [
              {
                "nombre": "proveedor",
                "tipo": "text",
                "requerido": true
              },
              {
                "nombre": "tipo_certificacion",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Ej: ISO 9001, HACCP"
              }
            ]
            """;
    }

    private String buildFieldsDefinitionManualCalidad() {
        return """
            [
              {
                "nombre": "version",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Versión del manual"
              },
              {
                "nombre": "fecha_aprobacion",
                "tipo": "date",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionEstudiosValidacion() {
        return """
            [
              {
                "nombre": "proceso_validado",
                "tipo": "text",
                "requerido": true
              },
              {
                "nombre": "fecha_validacion",
                "tipo": "date",
                "requerido": true
              }
            ]
            """;
    }

    private String buildFieldsDefinitionAnalisisNutricional() {
        return """
            [
              {
                "nombre": "porcion",
                "tipo": "text",
                "requerido": true,
                "placeholder": "Ej: 30g"
              },
              {
                "nombre": "calorias",
                "tipo": "number",
                "requerido": true,
                "placeholder": "kcal por porción"
              },
              {
                "nombre": "grasas_totales",
                "tipo": "number",
                "requerido": true,
                "placeholder": "gramos"
              },
              {
                "nombre": "carbohidratos",
                "tipo": "number",
                "requerido": true,
                "placeholder": "gramos"
              },
              {
                "nombre": "proteinas",
                "tipo": "number",
                "requerido": true,
                "placeholder": "gramos"
              },
              {
                "nombre": "sodio",
                "tipo": "number",
                "requerido": true,
                "placeholder": "mg"
              }
            ]
            """;
    }

    // ========== DEFINICIONES DE FILE RULES ==========

    private String buildFileRulesPDF() {
        return """
            {
              "maxSize": 10485760,
              "allowedTypes": ["PDF"],
              "required": true
            }
            """;
    }

    private String buildFileRulesImages() {
        return """
            {
              "maxSize": 5242880,
              "allowedTypes": ["PDF", "JPG", "PNG"],
              "required": true
            }
            """;
    }

    private String buildFileRulesMultiple() {
        return """
            {
              "maxSize": 10485760,
              "allowedTypes": ["PDF", "JPG", "PNG", "DOCX"],
              "required": false
            }
            """;
    }
}

