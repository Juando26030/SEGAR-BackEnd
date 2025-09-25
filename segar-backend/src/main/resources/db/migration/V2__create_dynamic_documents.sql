-- Migración para módulo de documentos dinámicos
-- Crea las nuevas tablas para plantillas e instancias de documentos
-- Mantiene compatibilidad con la tabla Documento existente

-- Tabla para plantillas de documentos dinámicos
CREATE TABLE IF NOT EXISTS document_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    fields_definition CLOB,
    file_rules CLOB,
    version INT DEFAULT 1,
    active BOOLEAN DEFAULT TRUE,
    required BOOLEAN DEFAULT FALSE,
    display_order INT,
    categoria_riesgo VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

-- Tabla para tipos de trámite aplicables a plantillas
CREATE TABLE IF NOT EXISTS template_tramite_types (
    template_id BIGINT NOT NULL,
    tramite_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (template_id, tramite_type),
    FOREIGN KEY (template_id) REFERENCES document_template(id) ON DELETE CASCADE
);

-- Tabla para instancias de documentos
CREATE TABLE IF NOT EXISTS document_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    tramite_id BIGINT,
    empresa_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT',
    filled_data CLOB,
    file_url VARCHAR(1000),
    file_mime VARCHAR(255),
    file_size BIGINT,
    storage_key VARCHAR(1000),
    metadata CLOB,
    version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    FOREIGN KEY (template_id) REFERENCES document_template(id),
    FOREIGN KEY (tramite_id) REFERENCES tramite(id)
);

-- Extender tabla Documento existente para integración gradual
-- Agregar campos opcionales para compatibilidad con nuevo sistema
ALTER TABLE documento ADD COLUMN IF NOT EXISTS template_id BIGINT;
ALTER TABLE documento ADD COLUMN IF NOT EXISTS instance_id BIGINT;
ALTER TABLE documento ADD COLUMN IF NOT EXISTS metadata CLOB;
ALTER TABLE documento ADD COLUMN IF NOT EXISTS storage_key VARCHAR(500);

-- Agregar foreign keys para la tabla documento extendida
ALTER TABLE documento ADD CONSTRAINT IF NOT EXISTS fk_documento_template
    FOREIGN KEY (template_id) REFERENCES document_template(id);
ALTER TABLE documento ADD CONSTRAINT IF NOT EXISTS fk_documento_instance
    FOREIGN KEY (instance_id) REFERENCES document_instance(id);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_document_template_code ON document_template(code);
CREATE INDEX IF NOT EXISTS idx_document_template_active ON document_template(active);
CREATE INDEX IF NOT EXISTS idx_document_instance_tramite ON document_instance(tramite_id);
CREATE INDEX IF NOT EXISTS idx_document_instance_empresa ON document_instance(empresa_id);
CREATE INDEX IF NOT EXISTS idx_document_instance_status ON document_instance(status);
CREATE INDEX IF NOT EXISTS idx_document_instance_template ON document_instance(template_id);
