-- Schema SQL para el sistema SEGAR - Paso 5: Radicación de la Solicitud
-- Base de datos H2 para pruebas

-- Tabla de productos (ya existe, pero incluida para referencia)
CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    especificaciones TEXT,
    referencia VARCHAR(100),
    fabricante VARCHAR(255)
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    referencia_pago VARCHAR(100) UNIQUE,
    fecha_pago TIMESTAMP,
    concepto VARCHAR(500)
);

-- Tabla de solicitudes
CREATE TABLE IF NOT EXISTS solicitud (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    tipo_tramite VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    numero_radicado VARCHAR(100) UNIQUE,
    fecha_radicacion TIMESTAMP,
    observaciones TEXT,
    pago_id BIGINT,
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (pago_id) REFERENCES pago(id)
);

-- Tabla de documentos
CREATE TABLE IF NOT EXISTS documento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tamanio_archivo BIGINT,
    tipo_mime VARCHAR(100),
    fecha_carga TIMESTAMP,
    solicitud_id BIGINT,
    obligatorio BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (solicitud_id) REFERENCES solicitud(id)
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_solicitud_empresa ON solicitud(empresa_id);
CREATE INDEX IF NOT EXISTS idx_solicitud_estado ON solicitud(estado);
CREATE INDEX IF NOT EXISTS idx_solicitud_radicado ON solicitud(numero_radicado);
CREATE INDEX IF NOT EXISTS idx_documento_solicitud ON documento(solicitud_id);
CREATE INDEX IF NOT EXISTS idx_pago_referencia ON pago(referencia_pago);
