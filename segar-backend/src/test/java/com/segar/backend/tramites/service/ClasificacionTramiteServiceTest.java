package com.segar.backend.tramites.service;

import com.segar.backend.tramites.api.dto.*;
import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO.NivelRiesgo;
import com.segar.backend.tramites.api.dto.ClasificacionProductoDTO.TipoAccion;
import com.segar.backend.tramites.api.dto.ResultadoClasificacionDTO.TipoTramiteINVIMA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ClasificacionTramiteService
 * Verifica la lógica de clasificación de trámites INVIMA según las reglas de negocio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Clasificación de Trámites INVIMA - Tests")
class ClasificacionTramiteServiceTest {

    @InjectMocks
    private ClasificacionTramiteService clasificacionService;

    private ClasificacionProductoDTO clasificacionBase;

    @BeforeEach
    void setUp() {
        // Configuración base para cada test
        clasificacionBase = ClasificacionProductoDTO.builder()
                .categoria("Alimentos procesados")
                .poblacionObjetivo("población general")
                .procesamiento("pasteurizado")
                .nivelRiesgo(NivelRiesgo.BAJO)
                .esImportado(false)
                .tipoAccion(TipoAccion.REGISTRO)
                .build();
    }

    // ==================== TESTS DE REGLAS DE CLASIFICACIÓN ====================

    @Test
    @DisplayName("REGLA 1: Población vulnerable debe resultar en RSA")
    void testPoblacionVulnerable_DebeSerRSA() {
        // Given
        clasificacionBase.setPoblacionObjetivo("infantil");

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA);
        assertThat(resultado.getAdvertencias())
                .anyMatch(adv -> adv.contains("población vulnerable"));
    }

    @ParameterizedTest
    @MethodSource("proveerPoblacionesVulnerables")
    @DisplayName("REGLA 1: Diferentes poblaciones vulnerables deben ser RSA")
    void testDiferentesPoblacionesVulnerables(String poblacion) {
        // Given
        clasificacionBase.setPoblacionObjetivo(poblacion);
        clasificacionBase.setNivelRiesgo(NivelRiesgo.BAJO); // Aunque sea bajo riesgo

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite())
                .as("Población '%s' debe clasificar como RSA", poblacion)
                .isEqualTo(TipoTramiteINVIMA.RSA);
    }

    static Stream<Arguments> proveerPoblacionesVulnerables() {
        return Stream.of(
                Arguments.of("infantil"),
                Arguments.of("gestantes"),
                Arguments.of("adultos mayores"),
                Arguments.of("tercera-edad"),
                Arguments.of("bebés")
        );
    }

    @Test
    @DisplayName("REGLA 2: Procesamiento de alto riesgo debe resultar en RSA")
    void testProcesamientoAltoRiesgo_DebeSerRSA() {
        // Given
        clasificacionBase.setProcesamiento("esterilizado");
        clasificacionBase.setNivelRiesgo(NivelRiesgo.BAJO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA);
        assertThat(resultado.getAdvertencias())
                .anyMatch(adv -> adv.contains("procesamiento"));
    }

    @Test
    @DisplayName("REGLA 3: Riesgo alto explícito debe resultar en RSA")
    void testRiesgoAltoExplicito_DebeSerRSA() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.ALTO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA);
    }

    @Test
    @DisplayName("REGLA 4: Categoría alto riesgo + riesgo medio = RSA")
    void testCategoriaAltoRiesgoConMedio_DebeSerRSA() {
        // Given
        clasificacionBase.setCategoria("lácteos");
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA);
    }

    @Test
    @DisplayName("REGLA 5: Producto importado con riesgo medio debe ser PSA mínimo")
    void testProductoImportadoRiesgoMedio_DebeSerPSA() {
        // Given
        clasificacionBase.setEsImportado(true);
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite())
                .as("Producto importado con riesgo medio debe ser al menos PSA")
                .isIn(TipoTramiteINVIMA.PSA, TipoTramiteINVIMA.RSA);
    }

    @Test
    @DisplayName("REGLA 6: Riesgo medio sin otros factores debe ser PSA")
    void testRiesgoMedio_DebeSerPSA() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);
        clasificacionBase.setCategoria("bebidas");

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.PSA);
    }

    @Test
    @DisplayName("REGLA 7: Riesgo bajo con población general debe ser NSO")
    void testRiesgoBajoPoblacionGeneral_DebeSerNSO() {
        // Given - Ya está configurado en clasificacionBase

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.NSO);
    }

    // ==================== TESTS DE DOCUMENTOS REQUERIDOS ====================

    @Test
    @DisplayName("NSO debe incluir 5 documentos básicos")
    void testNSO_DebeIncluir5DocumentosBasicos() {
        // Given - clasificacionBase ya está configurada para NSO

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getDocumentos()).hasSize(5);
        assertThat(resultado.getDocumentos())
                .extracting(DocumentoRequeridoDTO::getId)
                .contains(
                        "certificado_existencia",
                        "ficha_tecnica_basica",
                        "etiqueta_digital",
                        "comprobante_pago"
                );
    }

    @Test
    @DisplayName("PSA debe incluir documentos básicos + análisis")
    void testPSA_DebeIncluirDocumentosBasicosYAnalisis() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.PSA);
        assertThat(resultado.getDocumentos().size()).isGreaterThanOrEqualTo(10);
        assertThat(resultado.getDocumentos())
                .extracting(DocumentoRequeridoDTO::getId)
                .contains(
                        "analisis_fisicoquimico",
                        "analisis_microbiologico",
                        "certificacion_bpm"
                );
    }

    @Test
    @DisplayName("RSA debe incluir todos los documentos incluidos HACCP")
    void testRSA_DebeIncluirDocumentosHACCP() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.ALTO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA);
        assertThat(resultado.getDocumentos())
                .extracting(DocumentoRequeridoDTO::getId)
                .contains(
                        "estudios_estabilidad",
                        "certificacion_haccp",
                        "plan_haccp"
                );
    }

    @Test
    @DisplayName("Producto importado debe incluir documentos de importación")
    void testProductoImportado_DebeIncluirDocumentosEspecificos() {
        // Given
        clasificacionBase.setEsImportado(true);
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getDocumentos())
                .extracting(DocumentoRequeridoDTO::getId)
                .contains(
                        "certificado_venta_libre",
                        "autorizacion_fabricante",
                        "traducciones_oficiales"
                );
    }

    @Test
    @DisplayName("Renovación debe incluir registro anterior")
    void testRenovacion_DebeIncluirRegistroAnterior() {
        // Given
        clasificacionBase.setTipoAccion(TipoAccion.RENOVACION);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getDocumentos())
                .extracting(DocumentoRequeridoDTO::getId)
                .contains("registro_anterior");
    }

    // ==================== TESTS DE ADVERTENCIAS ====================

    @Test
    @DisplayName("RSA debe generar advertencias específicas")
    void testRSA_DebeGenerarAdvertencias() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.ALTO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getAdvertencias())
                .isNotEmpty()
                .anyMatch(adv -> adv.contains("estudios de estabilidad"))
                .anyMatch(adv -> adv.contains("HACCP"));
    }

    @Test
    @DisplayName("Producto importado debe generar advertencia de documentación adicional")
    void testProductoImportado_DebeAdvertirDocumentacionAdicional() {
        // Given
        clasificacionBase.setEsImportado(true);
        clasificacionBase.setNivelRiesgo(NivelRiesgo.ALTO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getAdvertencias())
                .anyMatch(adv -> adv.contains("importados"))
                .anyMatch(adv -> adv.contains("traducirse"));
    }

    // ==================== TESTS DE TIEMPOS Y COSTOS ====================

    @Test
    @DisplayName("NSO debe tener el menor tiempo estimado")
    void testNSO_TiempoMenor() {
        // Given - clasificacionBase configurada para NSO

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTiempoEstimado()).isEqualTo("15-30 días hábiles");
        assertThat(resultado.getCostoEstimado()).isEqualTo("1-2 SMMLV");
    }

    @Test
    @DisplayName("PSA debe tener tiempo medio")
    void testPSA_TiempoMedio() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTiempoEstimado()).isEqualTo("30-60 días hábiles");
        assertThat(resultado.getCostoEstimado()).isEqualTo("3-5 SMMLV");
    }

    @Test
    @DisplayName("RSA debe tener el mayor tiempo estimado")
    void testRSA_TiempoMayor() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.ALTO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTiempoEstimado()).isEqualTo("60-90 días hábiles");
        assertThat(resultado.getCostoEstimado()).isEqualTo("8-15 SMMLV");
    }

    // ==================== TESTS DE INTEGRACIÓN COMPLETOS ====================

    @Test
    @DisplayName("Integración: Producto lácteo infantil debe ser RSA con todos documentos")
    void testIntegracion_LacteoInfantil() {
        // Given
        ClasificacionProductoDTO clasificacion = ClasificacionProductoDTO.builder()
                .categoria("lácteos")
                .poblacionObjetivo("infantil")
                .procesamiento("pasteurizado")
                .nivelRiesgo(NivelRiesgo.MEDIO)
                .esImportado(false)
                .tipoAccion(TipoAccion.REGISTRO)
                .build();

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacion);

        // Then
        assertAll(
                () -> assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA),
                () -> assertThat(resultado.getDocumentos().size()).isGreaterThan(15),
                () -> assertThat(resultado.getDocumentos())
                        .extracting(DocumentoRequeridoDTO::getId)
                        .contains("estudios_nutricionales"),
                () -> assertThat(resultado.getAdvertencias())
                        .anyMatch(adv -> adv.contains("población vulnerable")),
                () -> assertThat(resultado.getTiempoEstimado()).isEqualTo("60-90 días hábiles")
        );
    }

    @Test
    @DisplayName("Integración: Producto importado congelado alto riesgo")
    void testIntegracion_ImportadoCongeladoAltoRiesgo() {
        // Given
        ClasificacionProductoDTO clasificacion = ClasificacionProductoDTO.builder()
                .categoria("cárnicos")
                .poblacionObjetivo("población general")
                .procesamiento("congelado")
                .nivelRiesgo(NivelRiesgo.ALTO)
                .esImportado(true)
                .tipoAccion(TipoAccion.REGISTRO)
                .build();

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacion);

        // Then
        assertAll(
                () -> assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.RSA),
                () -> assertThat(resultado.getDocumentos())
                        .extracting(DocumentoRequeridoDTO::getId)
                        .contains(
                                "certificado_venta_libre",
                                "estudios_estabilidad",
                                "certificacion_haccp"
                        ),
                () -> assertThat(resultado.getAdvertencias())
                        .anyMatch(adv -> adv.contains("importados"))
                        .anyMatch(adv -> adv.contains("procesamiento"))
        );
    }

    @Test
    @DisplayName("Integración: Renovación de producto de bajo riesgo")
    void testIntegracion_RenovacionBajoRiesgo() {
        // Given
        ClasificacionProductoDTO clasificacion = ClasificacionProductoDTO.builder()
                .categoria("bebidas")
                .poblacionObjetivo("población general")
                .procesamiento("pasteurizado")
                .nivelRiesgo(NivelRiesgo.BAJO)
                .esImportado(false)
                .tipoAccion(TipoAccion.RENOVACION)
                .build();

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacion);

        // Then
        assertAll(
                () -> assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.NSO),
                () -> assertThat(resultado.getDocumentos())
                        .extracting(DocumentoRequeridoDTO::getId)
                        .contains("registro_anterior"),
                () -> assertThat(resultado.getDocumentos().size()).isGreaterThan(5)
        );
    }

    // ==================== TESTS DE CASOS EDGE ====================

    @Test
    @DisplayName("Edge Case: Valores nulos no deben causar excepciones")
    void testValoresNulos_NoDebeLanzarExcepcion() {
        // Given
        ClasificacionProductoDTO clasificacion = ClasificacionProductoDTO.builder()
                .categoria(null)
                .poblacionObjetivo(null)
                .procesamiento(null)
                .nivelRiesgo(NivelRiesgo.BAJO)
                .esImportado(null)
                .tipoAccion(TipoAccion.REGISTRO)
                .build();

        // When & Then
        assertThatCode(() -> clasificacionService.clasificarProducto(clasificacion))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Edge Case: Strings vacíos deben clasificarse como NSO por defecto")
    void testStringsVacios_DebeSerNSO() {
        // Given
        ClasificacionProductoDTO clasificacion = ClasificacionProductoDTO.builder()
                .categoria("")
                .poblacionObjetivo("")
                .procesamiento("")
                .nivelRiesgo(NivelRiesgo.BAJO)
                .esImportado(false)
                .tipoAccion(TipoAccion.REGISTRO)
                .build();

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacion);

        // Then
        assertThat(resultado.getTramite()).isEqualTo(TipoTramiteINVIMA.NSO);
    }

    @Test
    @DisplayName("Resultado debe contener descripción del trámite")
    void testResultado_DebeContenerDescripcion() {
        // Given - clasificacionBase

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getTramiteDescripcion())
                .isNotNull()
                .isNotEmpty()
                .contains("Notificación Sanitaria Obligatoria");
    }

    @Test
    @DisplayName("Documentos deben tener orden correcto")
    void testDocumentos_DebenTenerOrdenCorrecto() {
        // Given
        clasificacionBase.setNivelRiesgo(NivelRiesgo.MEDIO);

        // When
        ResultadoClasificacionDTO resultado = clasificacionService.clasificarProducto(clasificacionBase);

        // Then
        assertThat(resultado.getDocumentos())
                .isSortedAccordingTo((d1, d2) -> Integer.compare(d1.getOrden(), d2.getOrden()));
    }
}
