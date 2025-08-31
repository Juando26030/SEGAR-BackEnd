-- Datos de ejemplo para el sistema SEGAR - Paso 5: Radicación de la Solicitud
-- Base de datos H2 para pruebas

-- Insertar productos de ejemplo (complementando los existentes)
INSERT INTO producto (id, nombre, descripcion, especificaciones, referencia, fabricante) VALUES
(1, 'Yogurt Natural', 'Yogurt natural sin azúcar añadida', 'Contenido graso 3.5%, proteína 4g por 100ml', 'YOG-001', 'Lácteos del Valle S.A.S.'),
(2, 'Mermelada de Fresa', 'Mermelada artesanal de fresa', 'Sin conservantes artificiales, 65% fruta', 'MER-002', 'Dulces Tradicionales Ltda.'),
(3, 'Aceite de Oliva Extra Virgen', 'Aceite de oliva primera extracción en frío', 'Acidez máxima 0.3%, origen español', 'ACE-003', 'Gourmet Foods Colombia S.A.S.')
ON DUPLICATE KEY UPDATE id=id;

-- Insertar pagos de ejemplo con empresaId
INSERT INTO pago (id, empresa_id, monto, metodo_pago, estado, referencia_pago, fecha_pago, concepto) VALUES
(1, 1001, 1250000.00, 'TARJETA_CREDITO', 'APROBADO', 'PAY-2024-001', '2024-08-20 10:30:00', 'Pago tarifa registro sanitario - Yogurt Natural'),
(2, 1002, 890000.00, 'PSE', 'APROBADO', 'PAY-2024-002', '2024-08-21 14:15:00', 'Pago tarifa registro sanitario - Mermelada de Fresa'),
(3, 1003, 1150000.00, 'TRANSFERENCIA_BANCARIA', 'APROBADO', 'PAY-2024-003', '2024-08-22 09:45:00', 'Pago tarifa registro sanitario - Aceite de Oliva'),
(4, 1004, 750000.00, 'TARJETA_DEBITO', 'RECHAZADO', 'PAY-2024-004', '2024-08-23 16:20:00', 'Pago tarifa renovación registro - Producto rechazado'),
(5, 1002, 450000.00, 'PSE', 'PENDIENTE', 'PAY-2024-005', '2024-08-24 11:00:00', 'Pago adicional pendiente')
ON DUPLICATE KEY UPDATE id=id;

-- Insertar solicitudes de ejemplo para testing del Paso 5
INSERT INTO solicitud (id, empresa_id, producto_id, tipo_tramite, estado, numero_radicado, fecha_radicacion, observaciones, pago_id) VALUES
-- Solicitud ya radicada (para consultas)
(1, 1001, 1, 'REGISTRO', 'RADICADA', 'INV-20240820-000001', '2024-08-20 11:30:00', 'Solicitud de registro sanitario para yogurt natural - YA RADICADA', 1),
-- Solicitud pendiente con todo listo para radicar (para testing de radicación exitosa)
(2, 1002, 2, 'REGISTRO', 'PENDIENTE', NULL, NULL, 'Solicitud lista para radicar - documentos y pago completos', 2),
-- Solicitud pendiente con pago aprobado pero documentos incompletos (para testing de validación)
(3, 1003, 3, 'REGISTRO', 'PENDIENTE', NULL, NULL, 'Solicitud con documentos faltantes', 3)
ON DUPLICATE KEY UPDATE id=id;

-- Insertar documentos completos para solicitud ID 1 (Yogurt - ya radicada)
INSERT INTO documento (id, nombre_archivo, tipo_documento, ruta_archivo, tamanio_archivo, tipo_mime, fecha_carga, solicitud_id, obligatorio, archivo) VALUES
(1, 'certificado_constitucion_lacteos_valle.pdf', 'CERTIFICADO_CONSTITUCION', '/uploads/docs/cert_const_001.pdf', 2048576, 'application/pdf', '2024-08-20 09:00:00', 1, true, 'documento_simulado_certificado'),
(2, 'rut_lacteos_valle.pdf', 'RUT', '/uploads/docs/rut_001.pdf', 1024000, 'application/pdf', '2024-08-20 09:15:00', 1, true, 'documento_simulado_rut'),
(3, 'concepto_sanitario_planta.pdf', 'CONCEPTO_SANITARIO', '/uploads/docs/concepto_san_001.pdf', 3072000, 'application/pdf', '2024-08-20 09:30:00', 1, true, 'documento_simulado_concepto'),
(4, 'ficha_tecnica_yogurt.pdf', 'FICHA_TECNICA', '/uploads/docs/ficha_tec_001.pdf', 1536000, 'application/pdf', '2024-08-20 09:45:00', 1, true, 'documento_simulado_ficha'),
(5, 'etiqueta_yogurt_natural.pdf', 'ETIQUETA', '/uploads/docs/etiqueta_001.pdf', 2048000, 'application/pdf', '2024-08-20 10:00:00', 1, true, 'documento_simulado_etiqueta'),

-- Insertar documentos completos para solicitud ID 2 (Mermelada - lista para radicar)
(6, 'certificado_constitucion_dulces.pdf', 'CERTIFICADO_CONSTITUCION', '/uploads/docs/cert_const_002.pdf', 1856000, 'application/pdf', '2024-08-21 10:00:00', 2, true, 'documento_simulado_certificado_2'),
(7, 'rut_dulces_tradicionales.pdf', 'RUT', '/uploads/docs/rut_002.pdf', 768000, 'application/pdf', '2024-08-21 10:15:00', 2, true, 'documento_simulado_rut_2'),
(8, 'concepto_sanitario_dulces.pdf', 'CONCEPTO_SANITARIO', '/uploads/docs/concepto_san_002.pdf', 2304000, 'application/pdf', '2024-08-21 10:30:00', 2, true, 'documento_simulado_concepto_2'),
(9, 'ficha_tecnica_mermelada.pdf', 'FICHA_TECNICA', '/uploads/docs/ficha_tec_002.pdf', 1280000, 'application/pdf', '2024-08-21 10:45:00', 2, true, 'documento_simulado_ficha_2'),
(10, 'etiqueta_mermelada_fresa.pdf', 'ETIQUETA', '/uploads/docs/etiqueta_002.pdf', 1920000, 'application/pdf', '2024-08-21 11:00:00', 2, true, 'documento_simulado_etiqueta_2'),

-- Insertar documentos INCOMPLETOS para solicitud ID 3 (Aceite - para testing de validación)
(11, 'certificado_constitucion_gourmet.pdf', 'CERTIFICADO_CONSTITUCION', '/uploads/docs/cert_const_003.pdf', 2100000, 'application/pdf', '2024-08-22 08:00:00', 3, true, 'documento_simulado_certificado_3'),
(12, 'rut_gourmet_foods.pdf', 'RUT', '/uploads/docs/rut_003.pdf', 850000, 'application/pdf', '2024-08-22 08:15:00', 3, true, 'documento_simulado_rut_3')
-- Nota: Faltan documentos obligatorios para la solicitud 3 (concepto sanitario, ficha técnica, etiqueta)

ON DUPLICATE KEY UPDATE id=id;

-- Insertar documentos disponibles sin asignar (para testing de disponibilidad)
INSERT INTO documento (id, nombre_archivo, tipo_documento, ruta_archivo, tamanio_archivo, tipo_mime, fecha_carga, solicitud_id, obligatorio, archivo) VALUES
(13, 'documento_disponible_1.pdf', 'CERTIFICADO_BPM', '/uploads/docs/disponible_001.pdf', 1500000, 'application/pdf', '2024-08-25 10:00:00', NULL, false, 'documento_disponible_simulado_1'),
(14, 'documento_disponible_2.pdf', 'ANALISIS_MICROBIOLOGICO', '/uploads/docs/disponible_002.pdf', 1800000, 'application/pdf', '2024-08-25 10:30:00', NULL, false, 'documento_disponible_simulado_2')
ON DUPLICATE KEY UPDATE id=id;
