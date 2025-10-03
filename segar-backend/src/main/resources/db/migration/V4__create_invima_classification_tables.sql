-- Migración V4: Tablas para sistema de clasificación INVIMA
-- Integra las estructuras de formularios NSO/PSA/RSA con el sistema de documentos dinámicos

-- =============================================
-- TABLAS DE CLASIFICACIÓN INVIMA
-- =============================================

-- Tabla para almacenar clasificaciones INVIMA realizadas
CREATE TABLE IF NOT EXISTS clasificacion_invima (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    tramite_id BIGINT,
    
    -- Campos de clasificación
    categoria_alimento VARCHAR(50) NOT NULL,
    nivel_riesgo VARCHAR(10) NOT NULL,
    poblacion_objetivo VARCHAR(50) NOT NULL,
    tipo_procesamiento VARCHAR(50) NOT NULL,
    origen_nacional BOOLEAN DEFAULT TRUE,
    
    -- Producto importado (campos adicionales)
    producto_importado BOOLEAN DEFAULT FALSE,
    pais_origen VARCHAR(100),
    fabricante_extranjero VARCHAR(300),
    
    -- Resultado de clasificación
    tipo_tramite_resultante VARCHAR(10) NOT NULL, -- NSO, PSA, RSA
    es_combinacion_valida BOOLEAN DEFAULT TRUE,
    observaciones_validacion TEXT,
    
    -- Metadatos
    fecha_clasificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    clasificado_por VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVA',
    
    FOREIGN KEY (tramite_id) REFERENCES tramite(id)
);

-- =============================================
-- TABLAS DE FORMULARIOS ESPECÍFICOS
-- =============================================

-- Tabla base para datos comunes de todos los formularios
CREATE TABLE IF NOT EXISTS formulario_base (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clasificacion_id BIGINT NOT NULL,
    
    -- Datos del trámite
    tipo_tramite VARCHAR(10) NOT NULL, -- NSO, PSA, RSA
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_radicacion VARCHAR(100) UNIQUE,
    
    -- Datos del titular
    razon_social VARCHAR(300) NOT NULL,
    nit VARCHAR(20) NOT NULL,
    direccion_principal TEXT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    representante_legal VARCHAR(200) NOT NULL,
    
    -- Datos del establecimiento
    nombre_establecimiento VARCHAR(300) NOT NULL,
    direccion_establecimiento TEXT NOT NULL,
    telefono_establecimiento VARCHAR(20),
    
    -- Datos del producto
    nombre_producto VARCHAR(300) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    presentaciones TEXT, -- JSON con presentaciones
    
    -- Campos generales
    observaciones TEXT,
    estado VARCHAR(20) DEFAULT 'BORRADOR',
    
    FOREIGN KEY (clasificacion_id) REFERENCES clasificacion_invima(id)
);

-- Tabla específica para campos adicionales de PSA
CREATE TABLE IF NOT EXISTS formulario_psa_adicional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    formulario_base_id BIGINT NOT NULL,
    
    -- Campos específicos de PSA
    proceso_productivo TEXT,
    plan_muestreo TEXT,
    vida_util_producto INTEGER,
    condiciones_conservacion TEXT,
    poblacion_dirigida TEXT,
    
    -- Información nutricional
    informacion_nutricional TEXT, -- JSON
    declaraciones_nutricionales TEXT,
    
    -- Controles de calidad
    controles_calidad TEXT,
    laboratorio_control VARCHAR(300),
    
    FOREIGN KEY (formulario_base_id) REFERENCES formulario_base(id) ON DELETE CASCADE
);

-- Tabla específica para campos adicionales de RSA
CREATE TABLE IF NOT EXISTS formulario_rsa_adicional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    formulario_base_id BIGINT NOT NULL,
    
    -- Campos específicos de RSA (hereda PSA + adicionales)
    estudios_estabilidad TEXT,
    estudios_nutricionales TEXT,
    certificaciones_especiales TEXT,
    
    -- Población especial
    justificacion_poblacion_especial TEXT,
    estudios_seguridad TEXT,
    advertencias_especiales TEXT,
    
    -- Ingredientes funcionales
    ingredientes_funcionales TEXT, -- JSON
    sustancias_bioactivas TEXT,
    
    -- Validaciones adicionales
    autorizaciones_ingredientes TEXT,
    certificado_organico BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (formulario_base_id) REFERENCES formulario_base(id) ON DELETE CASCADE
);

-- =============================================
-- INTEGRACIÓN CON SISTEMA DE DOCUMENTOS
-- =============================================

-- Tabla para vincular formularios con documentos requeridos
CREATE TABLE IF NOT EXISTS formulario_documento_requerido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    formulario_base_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    es_obligatorio BOOLEAN DEFAULT TRUE,
    estado_documento VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, CARGADO, VALIDADO, RECHAZADO
    documento_instance_id BIGINT,
    observaciones VARCHAR(500),
    
    FOREIGN KEY (formulario_base_id) REFERENCES formulario_base(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES document_template(id),
    FOREIGN KEY (documento_instance_id) REFERENCES document_instance(id)
);

-- =============================================
-- TABLAS DE MATRIZ DE CLASIFICACIÓN
-- =============================================

-- Tabla para reglas de clasificación (para validaciones automáticas)
CREATE TABLE IF NOT EXISTS matriz_clasificacion_reglas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    categoria_alimento VARCHAR(50) NOT NULL,
    nivel_riesgo VARCHAR(10) NOT NULL,
    poblacion_objetivo VARCHAR(50) NOT NULL,
    tipo_procesamiento VARCHAR(50) NOT NULL,
    
    -- Resultado de la regla
    tipo_tramite_resultado VARCHAR(10) NOT NULL,
    es_combinacion_valida BOOLEAN DEFAULT TRUE,
    observacion_regla VARCHAR(500),
    
    -- Metadatos de regla
    prioridad INTEGER DEFAULT 1,
    activa BOOLEAN DEFAULT TRUE,
    creada_por VARCHAR(255) DEFAULT 'SYSTEM',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- ÍNDICES PARA PERFORMANCE
-- =============================================

CREATE INDEX IF NOT EXISTS idx_clasificacion_empresa ON clasificacion_invima(empresa_id);
CREATE INDEX IF NOT EXISTS idx_clasificacion_tramite ON clasificacion_invima(tramite_id);
CREATE INDEX IF NOT EXISTS idx_clasificacion_tipo ON clasificacion_invima(tipo_tramite_resultante);
CREATE INDEX IF NOT EXISTS idx_clasificacion_fecha ON clasificacion_invima(fecha_clasificacion);

CREATE INDEX IF NOT EXISTS idx_formulario_base_clasificacion ON formulario_base(clasificacion_id);
CREATE INDEX IF NOT EXISTS idx_formulario_base_tipo ON formulario_base(tipo_tramite);
CREATE INDEX IF NOT EXISTS idx_formulario_base_radicacion ON formulario_base(numero_radicacion);

CREATE INDEX IF NOT EXISTS idx_formulario_psa_base ON formulario_psa_adicional(formulario_base_id);
CREATE INDEX IF NOT EXISTS idx_formulario_rsa_base ON formulario_rsa_adicional(formulario_base_id);

CREATE INDEX IF NOT EXISTS idx_formulario_documento_base ON formulario_documento_requerido(formulario_base_id);
CREATE INDEX IF NOT EXISTS idx_formulario_documento_template ON formulario_documento_requerido(template_id);
CREATE INDEX IF NOT EXISTS idx_formulario_documento_estado ON formulario_documento_requerido(estado_documento);

CREATE INDEX IF NOT EXISTS idx_matriz_combinacion ON matriz_clasificacion_reglas(categoria_alimento, nivel_riesgo, poblacion_objetivo, tipo_procesamiento);
CREATE INDEX IF NOT EXISTS idx_matriz_resultado ON matriz_clasificacion_reglas(tipo_tramite_resultado);
CREATE INDEX IF NOT EXISTS idx_matriz_activa ON matriz_clasificacion_reglas(activa);