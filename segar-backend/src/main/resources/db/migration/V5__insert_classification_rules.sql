-- Migración V5: Datos iniciales para el sistema de clasificación INVIMA
-- Inserta reglas de clasificación basadas en la documentación INVIMA

-- =============================================
-- REGLAS DE CLASIFICACIÓN INVIMA
-- =============================================

-- Reglas para productos de PANADERÍA Y PASTELERÍA
-- Nivel de riesgo BAJO + Población general = NSO
INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('PANADERIA_PASTELERIA', 'BAJO', 'POBLACION_GENERAL', 'HORNEADO', 'NSO', true, 'Productos tradicionales de panadería sin aditivos especiales', 1);

-- Nivel de riesgo BAJO + Población infantil = CONTRADICCIÓN (debe ser ALTO)
INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('PANADERIA_PASTELERIA', 'BAJO', 'POBLACION_INFANTIL', 'USO_ADITIVOS', 'RSA', false, 'CONTRADICCIÓN: Población infantil requiere nivel de riesgo ALTO', 10);

-- Nivel de riesgo ALTO + Población infantil = RSA (Correcto)
INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('PANADERIA_PASTELERIA', 'ALTO', 'POBLACION_INFANTIL', 'USO_ADITIVOS', 'RSA', true, 'Productos para población infantil con aditivos requieren RSA', 1);

-- Nivel de riesgo MEDIO + Población general = PSA
INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('PANADERIA_PASTELERIA', 'MEDIO', 'POBLACION_GENERAL', 'USO_ADITIVOS', 'PSA', true, 'Productos con aditivos para población general', 1);

-- =============================================
-- REGLAS PARA LÁCTEOS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('LACTEOS', 'BAJO', 'POBLACION_GENERAL', 'PASTEURIZACION', 'NSO', true, 'Lácteos pasteurizados tradicionales', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('LACTEOS', 'ALTO', 'BEBES_MENORES_1_ANO', 'UHT_ESTERILIZACION', 'RSA', true, 'Fórmulas lácteas para bebés', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('LACTEOS', 'MEDIO', 'POBLACION_GENERAL', 'FERMENTACION_CONTROLADA', 'PSA', true, 'Productos lácteos fermentados', 1);

-- =============================================
-- REGLAS PARA CÁRNICOS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CARNICOS', 'ALTO', 'POBLACION_GENERAL', 'EMBUTIDOS_CURADOS', 'RSA', true, 'Productos cárnicos procesados requieren RSA', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CARNICOS', 'MEDIO', 'POBLACION_GENERAL', 'COCCION_SIMPLE', 'PSA', true, 'Cárnicos cocidos simples', 1);

-- =============================================
-- REGLAS PARA BEBIDAS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('BEBIDAS_NO_ALCOHOLICAS', 'BAJO', 'POBLACION_GENERAL', 'SIMPLE_MEZCLA', 'NSO', true, 'Bebidas simples sin aditivos especiales', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('BEBIDAS_NO_ALCOHOLICAS', 'MEDIO', 'POBLACION_DEPORTISTAS', 'USO_ADITIVOS', 'PSA', true, 'Bebidas deportivas con aditivos', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('BEBIDAS_NO_ALCOHOLICAS', 'ALTO', 'POBLACION_INFANTIL', 'FORTIFICACION_VITAMINAS', 'RSA', true, 'Bebidas fortificadas para niños', 1);

-- =============================================
-- REGLAS PARA CONSERVAS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CONSERVAS', 'ALTO', 'POBLACION_GENERAL', 'ESTERILIZACION_COMERCIAL', 'RSA', true, 'Conservas requieren proceso de esterilización validado', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CONSERVAS', 'MEDIO', 'POBLACION_GENERAL', 'ACIDIFICACION', 'PSA', true, 'Conservas acidificadas', 1);

-- =============================================
-- REGLAS PARA CEREALES
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CEREALES_DERIVADOS', 'BAJO', 'POBLACION_GENERAL', 'MOLIENDA_SIMPLE', 'NSO', true, 'Harinas y cereales básicos', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CEREALES_DERIVADOS', 'ALTO', 'BEBES_MENORES_1_ANO', 'FORTIFICACION_VITAMINAS', 'RSA', true, 'Cereales fortificados para bebés', 1);

-- =============================================
-- REGLAS PARA ACEITES Y GRASAS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('ACEITES_GRASAS', 'BAJO', 'POBLACION_GENERAL', 'EXTRACCION_SIMPLE', 'NSO', true, 'Aceites vegetales básicos', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('ACEITES_GRASAS', 'MEDIO', 'POBLACION_GENERAL', 'REFINACION_INDUSTRIAL', 'PSA', true, 'Aceites refinados industrialmente', 1);

-- =============================================
-- REGLAS PARA FRUTAS Y VEGETALES
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('FRUTAS_VEGETALES_PROCESADOS', 'BAJO', 'POBLACION_GENERAL', 'DESHIDRATACION', 'NSO', true, 'Frutas y vegetales deshidratados', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('FRUTAS_VEGETALES_PROCESADOS', 'MEDIO', 'POBLACION_GENERAL', 'CONGELACION_IQF', 'PSA', true, 'Productos congelados IQF', 1);

-- =============================================
-- REGLAS PARA SUPLEMENTOS
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('SUPLEMENTOS_DIETETICOS', 'ALTO', 'POBLACION_GENERAL', 'ENCAPSULACION', 'RSA', true, 'Todos los suplementos dietéticos requieren RSA', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('SUPLEMENTOS_DIETETICOS', 'ALTO', 'MUJERES_GESTANTES', 'TABLETEADO', 'RSA', true, 'Suplementos para embarazadas requieren RSA', 1);

-- =============================================
-- REGLAS PARA EDULCORANTES
-- =============================================

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('EDULCORANTES', 'ALTO', 'POBLACION_GENERAL', 'SINTESIS_QUIMICA', 'RSA', true, 'Edulcorantes artificiales requieren RSA', 1);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('EDULCORANTES', 'MEDIO', 'POBLACION_GENERAL', 'EXTRACCION_NATURAL', 'PSA', true, 'Edulcorantes naturales procesados', 1);

-- =============================================
-- REGLAS DE VALIDACIÓN CRUZADA (CONTRADICCIONES)
-- =============================================

-- Todas las poblaciones sensibles con nivel bajo son contradicciones
INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CUALQUIER_CATEGORIA', 'BAJO', 'MUJERES_GESTANTES', 'CUALQUIER_PROCESAMIENTO', 'RSA', false, 'CONTRADICCIÓN: Embarazadas requieren nivel ALTO', 20);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CUALQUIER_CATEGORIA', 'BAJO', 'MUJERES_LACTANTES', 'CUALQUIER_PROCESAMIENTO', 'RSA', false, 'CONTRADICCIÓN: Lactantes requieren nivel ALTO', 20);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CUALQUIER_CATEGORIA', 'BAJO', 'POBLACION_ADULTO_MAYOR', 'CUALQUIER_PROCESAMIENTO', 'RSA', false, 'CONTRADICCIÓN: Adultos mayores requieren nivel ALTO', 20);

INSERT INTO matriz_clasificacion_reglas (categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento, tipo_tramite_resultado, es_combinacion_valida, observacion_regla, prioridad)
VALUES ('CUALQUIER_CATEGORIA', 'BAJO', 'DIETAS_ESPECIALES', 'CUALQUIER_PROCESAMIENTO', 'RSA', false, 'CONTRADICCIÓN: Dietas especiales requieren nivel ALTO', 20);