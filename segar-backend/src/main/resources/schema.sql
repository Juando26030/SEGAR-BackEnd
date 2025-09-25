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

-- Tabla de trámites (si no existe)
CREATE TABLE IF NOT EXISTS tramite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_radicado VARCHAR(100) UNIQUE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    empresa_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    solicitud_id BIGINT,
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (solicitud_id) REFERENCES solicitud(id)
);

-- Tabla de resoluciones INVIMA
CREATE TABLE IF NOT EXISTS resolucion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_resolucion VARCHAR(100) UNIQUE NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    autoridad VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    observaciones TEXT,
    tramite_id BIGINT NOT NULL,
    documento_url VARCHAR(500),
    fecha_notificacion TIMESTAMP,
    FOREIGN KEY (tramite_id) REFERENCES tramite(id)
);

-- Tabla de registros sanitarios
CREATE TABLE IF NOT EXISTS registro_sanitario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_registro VARCHAR(100) UNIQUE NOT NULL,
    fecha_expedicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP NOT NULL,
    producto_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'VIGENTE',
    resolucion_id BIGINT NOT NULL,
    documento_url VARCHAR(500),
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (resolucion_id) REFERENCES resolucion(id)
);

-- Tabla de historial de trámites
CREATE TABLE IF NOT EXISTS historial_tramite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tramite_id BIGINT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(100) NOT NULL,
    descripcion TEXT,
    usuario VARCHAR(100),
    estado VARCHAR(50),
    FOREIGN KEY (tramite_id) REFERENCES tramite(id)
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_solicitud_empresa ON solicitud(empresa_id);
CREATE INDEX IF NOT EXISTS idx_solicitud_estado ON solicitud(estado);
CREATE INDEX IF NOT EXISTS idx_solicitud_radicado ON solicitud(numero_radicado);
CREATE INDEX IF NOT EXISTS idx_documento_solicitud ON documento(solicitud_id);
CREATE INDEX IF NOT EXISTS idx_pago_referencia ON pago(referencia_pago);
-- Índices adicionales para el módulo de resolución
CREATE INDEX IF NOT EXISTS idx_resolucion_tramite ON resolucion(tramite_id);
CREATE INDEX IF NOT EXISTS idx_resolucion_numero ON resolucion(numero_resolucion);
CREATE INDEX IF NOT EXISTS idx_registro_producto ON registro_sanitario(producto_id);
CREATE INDEX IF NOT EXISTS idx_registro_empresa ON registro_sanitario(empresa_id);
CREATE INDEX IF NOT EXISTS idx_registro_numero ON registro_sanitario(numero_registro);
CREATE INDEX IF NOT EXISTS idx_historial_tramite ON historial_tramite(tramite_id);
CREATE INDEX IF NOT EXISTS idx_historial_fecha ON historial_tramite(fecha);
