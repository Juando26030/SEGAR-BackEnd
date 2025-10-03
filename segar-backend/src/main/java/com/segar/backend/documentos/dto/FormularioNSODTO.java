package com.segar.backend.documentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para formulario de Notificación Sanitaria Obligatoria (NSO)
 * Formulario oficial: ASS-NSA-FM097
 * Trámite para productos de bajo riesgo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormularioNSODTO {

    // 1. Datos del trámite
    @Builder.Default
    private TipoTramiteNSO tipoTramite = TipoTramiteNSO.NSO;
    private LocalDateTime fechaSolicitud;
    private String numeroRadicacion;

    // 2. Datos del titular
    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;
    
    @NotNull(message = "El tipo de persona es obligatorio")
    private TipoPersona tipoPersona;
    
    @NotBlank(message = "NIT o cédula es obligatorio")
    private String nitCedula;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccionCompleta;
    
    @NotBlank(message = "El municipio es obligatorio")
    private String municipio;
    
    @NotBlank(message = "El departamento es obligatorio")
    private String departamento;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String correoElectronico;

    // Representante legal
    @NotBlank(message = "El nombre del representante legal es obligatorio")
    private String nombreRepresentanteLegal;
    
    @NotBlank(message = "El documento del representante legal es obligatorio")
    private String documentoRepresentanteLegal;

    // Apoderado (opcional)
    private String nombreApoderado;
    private String documentoApoderado;

    // 3. Datos del establecimiento productor
    @NotBlank(message = "El nombre del establecimiento es obligatorio")
    private String nombreEstablecimiento;
    
    @NotBlank(message = "La dirección del establecimiento es obligatoria")
    private String direccionEstablecimiento;
    
    @NotBlank(message = "La ciudad del establecimiento es obligatoria")
    private String ciudadEstablecimiento;
    
    @NotBlank(message = "El departamento del establecimiento es obligatorio")
    private String departamentoEstablecimiento;
    
    @NotNull(message = "La actividad principal es obligatoria")
    private ActividadPrincipal actividadPrincipal;
    
    private String numeroHabilitacionSanitaria;

    // 3.1. Datos para productos importados (opcionales)
    private Boolean esProductoImportado = false;
    private String paisOrigen;
    private String nombreFabricanteExtranjero;
    private String direccionFabricanteExtranjero;
    private String nombreImportadorColombia;
    private String direccionImportadorColombia;
    private String numeroRegistroSanitarioPaisOrigen;
    private String vigenciaRegistroPaisOrigen;
    private Boolean tieneAutorizacionFabricante;
    private Boolean documentosEnEspanol;

    // 4. Clasificación del producto
    @NotBlank(message = "La categoría del alimento es obligatoria")
    private String categoriaAlimento;
    
    @Builder.Default
    private String nivelRiesgo = "BAJO"; // NSO solo aplica a bajo riesgo
    
    @Builder.Default
    private String poblacionObjetivo = "POBLACION_GENERAL"; // NSO típicamente para población general
    
    @NotBlank(message = "El tipo de procesamiento es obligatorio")
    private String tipoProcesamiento;

    // 5. Información del producto
    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;
    
    private String marca;
    
    @NotBlank(message = "La denominación específica es obligatoria")
    private String denominacionEspecifica;
    
    @NotNull(message = "La presentación comercial es obligatoria")
    private PresentacionComercialDTO presentacionComercial;
    
    private List<VarianteProductoDTO> variantes;
    
    @NotNull(message = "La vida útil es obligatoria")
    private Integer vidaUtilMeses;
    
    @NotBlank(message = "Las condiciones de conservación son obligatorias")
    private String condicionesConservacion;

    // 6. Composición
    @NotNull(message = "La lista de ingredientes es obligatoria")
    private List<IngredienteDTO> ingredientes;
    
    private List<AditivoDTO> aditivos;
    
    private List<String> alergenos;

    // 7. Información nutricional
    @NotNull(message = "La información nutricional es obligatoria")
    private InformacionNutricionalDTO informacionNutricional;

    // 8. Etiquetado
    private String archivoEtiqueta; // URL o path del archivo de etiqueta

    // Documentos adjuntos
    private List<DocumentoAdjuntoDTO> documentosAdjuntos;

    // Pago
    private String comprobantePageo;
    private Double montoTarifa;

    // Observaciones
    private String observaciones;
}

// Enums y DTOs auxiliares
enum TipoTramiteNSO {
    NSO
}

enum TipoPersona {
    NATURAL, JURIDICA
}

enum ActividadPrincipal {
    FABRICACION, ENVASADO, MEZCLA, ALMACENAMIENTO
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PresentacionComercialDTO {
    private String tipoEnvase; // bolsa, caja, frasco
    private String materialEnvase;
    private Double contenidoNeto;
    private String unidadMedida; // g, ml, kg
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class VarianteProductoDTO {
    private String sabor;
    private String tamano;
    private String formato;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class IngredienteDTO {
    private String nombre;
    private Double porcentaje;
    private Integer orden; // Orden decreciente por cantidad
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AditivoDTO {
    private String nombre;
    private String funcion; // antioxidante, conservante, colorante
    private Double dosis;
    private String referenciaNormativa;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class InformacionNutricionalDTO {
    private Double tamanoPorcion;
    private String unidadPorcion;
    private Integer porcionesPorEnvase;
    
    // Por porción
    private Double energiaKcalPorcion;
    private Double grasasTotalesPorcion;
    private Double grasasSaturadasPorcion;
    private Double grasasTransPorcion;
    private Double carbohidratosTotalesPorcion;
    private Double azucaresPorcion;
    private Double proteinasPorcion;
    private Double sodioPorcion;
    private Double fibraPorcion;
    
    // Por 100g/ml
    private Double energiaKcal100g;
    private Double grasasTotales100g;
    private Double grasasSaturadas100g;
    private Double grasasTrans100g;
    private Double carbohidratosTotales100g;
    private Double azucares100g;
    private Double proteinas100g;
    private Double sodio100g;
    private Double fibra100g;
    
    // Vitaminas y minerales (si aplica)
    private List<VitaminaMineralDTO> vitaminasMinerales;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class VitaminaMineralDTO {
    private String nombre;
    private Double cantidadPorPorcion;
    private Double cantidadPor100g;
    private String unidad;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class DocumentoAdjuntoDTO {
    private String nombre;
    private String tipo;
    private String archivo; // URL o path
    private Boolean esObligatorio;
}
