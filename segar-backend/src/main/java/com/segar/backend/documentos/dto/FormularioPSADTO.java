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
 * DTO para formulario de Permiso Sanitario (PSA)
 * Trámite para productos de riesgo medio
 * Extiende todos los campos de NSO más campos específicos de PSA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormularioPSADTO {

    // ========== HEREDA TODOS LOS CAMPOS DE NSO ==========
    
    // 1. Datos del trámite
    @Builder.Default
    private TipoTramitePSA tipoTramite = TipoTramitePSA.PSA;
    private LocalDateTime fechaSolicitud;
    private String numeroRadicacion;

    // 2. Datos del titular (iguales a NSO)
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
    @NotBlank(message = "El nombre del representante legal es obligatorio")
    private String nombreRepresentanteLegal;
    @NotBlank(message = "El documento del representante legal es obligatorio")
    private String documentoRepresentanteLegal;
    private String nombreApoderado;
    private String documentoApoderado;

    // 3. Datos del establecimiento productor (extendidos para PSA)
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
    
    // ¡NUEVO EN PSA! - Certificación BPM obligatoria
    @NotNull(message = "La certificación BPM es obligatoria para PSA")
    private CertificacionBPMDTO certificacionBPM;

    // 4. Clasificación del producto
    @NotBlank(message = "La categoría del alimento es obligatoria")
    private String categoriaAlimento;
    @Builder.Default
    private String nivelRiesgo = "MEDIO"; // PSA solo aplica a riesgo medio
    @NotBlank(message = "La población objetivo es obligatoria")
    private String poblacionObjetivo;
    @NotBlank(message = "El tipo de procesamiento es obligatorio")
    private String tipoProcesamiento;

    // 5. Información del producto (extendida para PSA)
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
    
    // ¡NUEVO EN PSA! - Condiciones de transporte detalladas
    @NotNull(message = "Las condiciones de transporte son obligatorias para PSA")
    private CondicionesTransporteDTO condicionesTransporte;

    // 6. Composición (extendida para PSA)
    @NotNull(message = "La lista de ingredientes es obligatoria")
    private List<IngredienteDTO> ingredientes;
    private List<AditivoDTO> aditivos;
    private List<String> alergenos;
    
    // ¡NUEVO EN PSA! - Origen de materias primas
    private List<OrigenMateriaPrimaDTO> origenMateriasPrimas;

    // 7. Información nutricional (igual que NSO)
    @NotNull(message = "La información nutricional es obligatoria")
    private InformacionNutricionalDTO informacionNutricional;

    // 8. Etiquetado (igual que NSO)
    private String archivoEtiqueta;

    // ========== CAMPOS ESPECÍFICOS DE PSA ==========

    // 9. Análisis fisicoquímicos (OBLIGATORIOS EN PSA)
    @NotNull(message = "Los análisis fisicoquímicos son obligatorios para PSA")
    private List<AnalisisFisicoquimicoDTO> analisisFisicoquimicos;

    // 10. Análisis microbiológicos (OBLIGATORIOS EN PSA)
    @NotNull(message = "Los análisis microbiológicos son obligatorios para PSA")
    private List<AnalisisMicrobiologicoDTO> analisisMicrobiologicos;

    // 11. Plan de Saneamiento BPM (OBLIGATORIO EN PSA)
    @NotNull(message = "El plan de saneamiento BPM es obligatorio para PSA")
    private PlanSaneamientoBPMDTO planSaneamientoBPM;

    // 12. Proceso tecnológico resumido
    private ProcesoTecnologicoDTO procesoTecnologico;

    // 13. Validación de proceso (para pasteurizados, esterilizados)
    private ValidacionProcesoDTO validacionProceso;

    // Documentos adjuntos (extendidos)
    private List<DocumentoAdjuntoDTO> documentosAdjuntos;

    // Pago
    private String comprobantePago;
    private Double montoTarifa;

    // Observaciones
    private String observaciones;
}

// Enums específicos para PSA
enum TipoTramitePSA {
    PSA
}

// DTOs específicos para PSA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CertificacionBPMDTO {
    private String numeroConcepto;
    private LocalDateTime fechaExpedicion;
    private String entidadExpedidora; // INVIMA o autoridad competente
    private LocalDateTime fechaVencimiento;
    private String archivoConcepto;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CondicionesTransporteDTO {
    private String temperaturaTransporte;
    private String tipoTransporte;
    private String sistemaRefrigeracion;
    private String proteccionContaminacion;
    private String tiempoMaximoTransporte;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrigenMateriaPrimaDTO {
    private String ingrediente;
    private String proveedor;
    private String paisOrigen;
    private String certificaciones; // Orgánico, HACCP, etc.
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AnalisisFisicoquimicoDTO {
    private String parametro; // pH, humedad, azúcares, actividad de agua
    private Double valor;
    private String unidad;
    private String metodoAnalisis;
    private String laboratorioAcreditado;
    private LocalDateTime fechaAnalisis;
    private String numeroInforme;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AnalisisMicrobiologicoDTO {
    private String parametro; // coliformes, E. coli, Salmonella, mohos y levaduras
    private String resultado;
    private String unidad; // UFC/g, NMP/g
    private String metodoAnalisis;
    private String laboratorioAcreditado;
    private LocalDateTime fechaAnalisis;
    private String numeroInforme;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PlanSaneamientoBPMDTO {
    // Programas BPM
    private ProgramaLimpiezaDTO programaLimpieza;
    private ProgramaPlagasDTO programaPlagas;
    private ProgramaAguaDTO programaAgua;
    private ProgramaResiduosDTO programaResiduos;
    private ProgramaPersonalDTO programaPersonal;
    private ProgramaMantenimientoDTO programaMantenimiento;
    private ProgramaCapacitacionDTO programaCapacitacion;
    private ProgramaTrazabilidadDTO programaTrazabilidad;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProcesoTecnologicoDTO {
    private String descripcionProceso;
    private String diagramaFlujo; // archivo
    private List<EtapaProcesoDTO> etapas;
    private List<PuntoControlDTO> puntosControl;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ValidacionProcesoDTO {
    private String tipoValidacion; // térmica, química, física
    private String parametrosControl;
    private String limitesOperacionales;
    private String registrosAsociados;
}

// DTOs para programas BPM (simplificados)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaLimpiezaDTO {
    private String procedimientos;
    private String frecuencias;
    private String responsables;
    private String productos;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaPlagasDTO {
    private String planControl;
    private String proveedor;
    private String registros;
    private String frecuencia;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaAguaDTO {
    private String fuenteAgua;
    private String tratamiento;
    private String monitoreoPotabilidad;
    private String registrosCalidad;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaResiduosDTO {
    private String clasificacionResiduos;
    private String almacenamiento;
    private String disposicionFinal;
    private String frecuenciaRecoleccion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaPersonalDTO {
    private String capacitacionHigiene;
    private String usoEPP;
    private String examenesPeriodicosSalud;
    private String registrosCapacitacion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaMantenimientoDTO {
    private String planMantenimiento;
    private String cronogramaActividades;
    private String registrosMantenimiento;
    private String calibracionEquipos;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaCapacitacionDTO {
    private String temarios;
    private String cronogramaCapacitacion;
    private String registrosAsistencia;
    private String evaluacionesCompetencia;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProgramaTrazabilidadDTO {
    private String sistemaIdentificacion;
    private String registrosLotes;
    private String procedimientoRetiro;
    private String simulacrosRetirada;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class EtapaProcesoDTO {
    private String nombre;
    private String descripcion;
    private String parametrosControl;
    private String instrumentosControl;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PuntoControlDTO {
    private String etapa;
    private String parametro;
    private String limite;
    private String frecuenciaMonitoreo;
}
