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
 * DTO para formulario de Registro Sanitario (RSA)
 * Trámite para productos de riesgo alto o poblaciones vulnerables
 * Extiende todos los campos de PSA más campos específicos de RSA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormularioRSADTO {

    // ========== HEREDA TODOS LOS CAMPOS DE PSA ==========
    
    // 1. Datos del trámite
    @Builder.Default
    private TipoTramiteRSA tipoTramite = TipoTramiteRSA.RSA;
    private LocalDateTime fechaSolicitud;
    private String numeroRadicacion;

    // 2. Datos del titular (iguales a PSA)
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

    // 3. Datos del establecimiento productor (extendidos para RSA)
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
    
    // Certificación BPM obligatoria (debe ser vigente de INVIMA)
    @NotNull(message = "La certificación BPM vigente de INVIMA es obligatoria para RSA")
    private CertificacionBPMDTO certificacionBPM;
    
    // ¡NUEVO EN RSA! - Sistema de inocuidad HACCP obligatorio
    @NotNull(message = "El sistema HACCP es obligatorio para RSA")
    private SistemaHACCPDTO sistemaHACCP;

    // 4. Clasificación del producto
    @NotBlank(message = "La categoría del alimento es obligatoria")
    private String categoriaAlimento;
    @Builder.Default
    private String nivelRiesgo = "ALTO"; // RSA solo aplica a riesgo alto
    @NotBlank(message = "La población objetivo es obligatoria")
    private String poblacionObjetivo;
    @NotBlank(message = "El tipo de procesamiento es obligatorio")
    private String tipoProcesamiento;

    // 5. Información del producto (extendida para RSA)
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
    private CondicionesTransporteDTO condicionesTransporte;

    // 6. Composición (extendida para RSA)
    @NotNull(message = "La lista de ingredientes es obligatoria")
    private List<IngredienteDTO> ingredientes;
    private List<AditivoDTO> aditivos;
    private List<String> alergenos;
    private List<OrigenMateriaPrimaDTO> origenMateriasPrimas;

    // 7. Información nutricional (igual que PSA)
    @NotNull(message = "La información nutricional es obligatoria")
    private InformacionNutricionalDTO informacionNutricional;

    // ¡NUEVO EN RSA! - Estudios nutricionales adicionales (para poblaciones sensibles)
    private List<EstudioNutricionalDTO> estudiosNutricionales;

    // 8. Etiquetado (extendido para RSA con advertencias especiales)
    private String archivoEtiqueta;
    private List<AdvertenciaEtiquetadoDTO> advertenciasEspeciales;

    // ========== CAMPOS HEREDADOS DE PSA ==========
    private List<AnalisisFisicoquimicoDTO> analisisFisicoquimicos;
    private List<AnalisisMicrobiologicoDTO> analisisMicrobiologicos;
    private PlanSaneamientoBPMDTO planSaneamientoBPM;

    // ========== CAMPOS ESPECÍFICOS DE RSA ==========

    // 9. Proceso de elaboración detallado (OBLIGATORIO EN RSA)
    @NotNull(message = "El proceso de elaboración detallado es obligatorio para RSA")
    private ProcesoElaboracionDTO procesoElaboracion;

    // 10. Estudios de estabilidad y vida útil (OBLIGATORIOS EN RSA)
    @NotNull(message = "Los estudios de estabilidad son obligatorios para RSA")
    private List<EstudioEstabilidadDTO> estudiosEstabilidad;

    // 11. Validación de proceso térmico (para esterilizados)
    private ValidacionProcesoTermicoDTO validacionProcesoTermico;

    // 12. Certificados de laboratorio acreditado (OBLIGATORIOS EN RSA)
    @NotNull(message = "Los certificados de laboratorio son obligatorios para RSA")
    private List<CertificadoLaboratorioDTO> certificadosLaboratorio;

    // 13. Plan HACCP implementado y validado (OBLIGATORIO EN RSA)
    @NotNull(message = "El plan HACCP implementado es obligatorio para RSA")
    private PlanHACCPDTO planHACCP;

    // 14. Condiciones especiales para poblaciones vulnerables
    private CondicionesPoblacionVulnerableDTO condicionesPoblacionVulnerable;

    // 15. Documentación para productos importados (si aplica)
    private DocumentacionImportadoDTO documentacionImportado;

    // Documentos adjuntos (los más extensos)
    private List<DocumentoAdjuntoDTO> documentosAdjuntos;

    // Pago
    private String comprobantePago;
    private Double montoTarifa;

    // Observaciones
    private String observaciones;
}

// Enums específicos para RSA
enum TipoTramiteRSA {
    RSA
}

// DTOs específicos para RSA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SistemaHACCPDTO {
    private String numeroConcepto;
    private LocalDateTime fechaImplementacion;
    private String entidadValidadora;
    private LocalDateTime fechaUltimaAuditoria;
    private String archivoConcepto;
    private EstadoHACCP estado;
}

enum EstadoHACCP {
    IMPLEMENTADO, VALIDADO, EN_PROCESO, PENDIENTE
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class EstudioNutricionalDTO {
    private String tipoEstudio; // composición, biodisponibilidad, funcionalidad
    private String objetivoEstudio;
    private String metodologia;
    private String resultados;
    private String conclusiones;
    private String laboratorioEjecutor;
    private LocalDateTime fechaEstudio;
    private String archivoInforme;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AdvertenciaEtiquetadoDTO {
    private String tipoAdvertencia;
    private String textoAdvertencia;
    private Boolean esObligatoria;
    private String poblacionAplicable;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProcesoElaboracionDTO {
    private String descripcionDetallada;
    private String diagramaFlujoDetallado; // archivo
    private List<EtapaElaboracionDTO> etapasElaboracion;
    private List<ParametroControlDTO> parametrosControl;
    private List<PuntoCriticoControlDTO> puntosCriticosControl;
    private List<MedidaCorrectivaDTO> medidasCorrectivas;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class EstudioEstabilidadDTO {
    private String tipoEstudio; // acelerado, tiempo real, predictivo
    private String condicionesAlmacenamiento; // temperatura, humedad, luz
    private List<String> parametrosEvaluados;
    private List<TiempoMuestreoDTO> tiemposMuestreo;
    private String conclusion;
    private Integer vidaUtilDeterminada;
    private String laboratorioEjecutor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String archivoInforme;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ValidacionProcesoTermicoDTO {
    private String tipoTratamiento; // pasteurización, esterilización
    private Double temperaturaObjetivo;
    private Double tiempoTratamiento;
    private String microorganismoObjetivo;
    private Double reduccionLogaritmica;
    private String equipoUtilizado;
    private String metodologiaValidacion;
    private List<String> registrosProceso;
    private String archivoValidacion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CertificadoLaboratorioDTO {
    private String tipoAnalisis;
    private String laboratorioAcreditado;
    private String numeroAcreditacion;
    private LocalDateTime fechaAnalisis;
    private String numeroInforme;
    private String archivo;
    private Boolean cumpleEspecificaciones;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PlanHACCPDTO {
    private String descripcionPlan;
    private List<PeligroIdentificadoDTO> peligrosIdentificados;
    private List<PuntoCriticoControlDTO> puntosCriticosControl;
    private List<MedidaCorrectivaDTO> medidasCorrectivas;
    private ProcedimientoVerificacionDTO procedimientoVerificacion;
    private SistemaRegistrosDTO sistemaRegistros;
    private LocalDateTime fechaImplementacion;
    private LocalDateTime fechaUltimaRevision;
    private String responsableHACCP;
    private String archivoPlan;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CondicionesPoblacionVulnerableDTO {
    private String grupoEtario;
    private String justificacionNutricional;
    private List<String> advertenciasObligatorias;
    private List<String> requisitosComposicion;
    private List<EstudioSeguridadDTO> estudiosSeguridad;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class DocumentacionImportadoDTO {
    private String paisOrigen;
    private String nombreFabricanteExtranjero;
    private String direccionFabricanteExtranjero;
    private String importadorColombia;
    private String autorizacionFabricante; // archivo
    private String certificadoVentaLibre; // archivo
    private String registroSanitarioPaisOrigen; // archivo
    private List<TraduccionOficialDTO> traduccionesOficiales;
}

// DTOs auxiliares adicionales para RSA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class EtapaElaboracionDTO {
    private String nombre;
    private String descripcionDetallada;
    private List<String> equiposUtilizados;
    private List<ParametroControlDTO> parametrosControl;
    private String tiempoEtapa;
    private String temperaturaEtapa;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ParametroControlDTO {
    private String parametro;
    private String valorObjetivo;
    private String rangoAceptable;
    private String metodoCrontol;
    private String frecuenciaMonitoreo;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PuntoCriticoControlDTO {
    private String identificador; // PCC1, PCC2, etc.
    private String etapaProceso;
    private String peligroControlado;
    private String limiteCritico;
    private String sistemaMonitoreo;
    private String frecuenciaMonitoreo;
    private String responsableMonitoreo;
    private String accionCorrectiva;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class MedidaCorrectivaDTO {
    private String puntoControl;
    private String desviacionDetectada;
    private String accionInmediata;
    private String accionPreventiva;
    private String responsable;
    private String registroAccion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TiempoMuestreoDTO {
    private Integer tiempoMeses;
    private String condicionAlmacenamiento;
    private List<String> parametrosEvaluados;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PeligroIdentificadoDTO {
    private String tipoPeligro; // biológico, químico, físico
    private String descripcion;
    private String etapaOcurrencia;
    private String severidad;
    private String probabilidad;
    private String medidaControl;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProcedimientoVerificacionDTO {
    private String descripcion;
    private String frecuenciaVerificacion;
    private String responsableVerificacion;
    private List<String> actividadesVerificacion;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SistemaRegistrosDTO {
    private String descripcionSistema;
    private List<String> tiposRegistros;
    private String tiempoRetencion;
    private String responsableMantenimiento;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class EstudioSeguridadDTO {
    private String tipoEstudio;
    private String poblacionEstudiada;
    private String duracionEstudio;
    private String resultados;
    private String conclusiones;
    private String archivoEstudio;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TraduccionOficialDTO {
    private String documentoOriginal;
    private String traduccionOficial; // archivo
    private String traductorOficial;
    private LocalDateTime fechaTraduccion;
}
