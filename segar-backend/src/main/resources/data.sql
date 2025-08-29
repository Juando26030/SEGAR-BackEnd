-- Datos de ejemplo para el sistema SEGAR - Paso 5: Radicación de la Solicitud
-- Base de datos H2 para pruebas

-- Insertar productos de ejemplo (complementando los existentes)
INSERT INTO producto (id, nombre, descripcion, especificaciones, referencia, fabricante) VALUES
(1, 'Yogurt Natural', 'Yogurt natural sin azúcar añadida', 'Contenido graso 3.5%, proteína 4g por 100ml', 'YOG-001', 'Lácteos del Valle S.A.S.'),
(2, 'Mermelada de Fresa', 'Mermelada artesanal de fresa', 'Sin conservantes artificiales, 65% fruta', 'MER-002', 'Dulces Tradicionales Ltda.'),
(3, 'Aceite de Oliva Extra Virgen', 'Aceite de oliva primera extracción en frío', 'Acidez máxima 0.3%, origen español', 'ACE-003', 'Gourmet Foods Colombia S.A.S.')
ON DUPLICATE KEY UPDATE id=id;

-- Insertar pagos de ejemplo
INSERT INTO pago (id, monto, metodo_pago, estado, referencia_pago, fecha_pago, concepto) VALUES
(1, 1250000.00, 'TARJETA_CREDITO', 'APROBADO', 'PAY-2024-001', '2024-08-20 10:30:00', 'Pago tarifa registro sanitario - Yogurt Natural'),
(2, 890000.00, 'PSE', 'APROBADO', 'PAY-2024-002', '2024-08-21 14:15:00', 'Pago tarifa registro sanitario - Mermelada de Fresa'),
(3, 1150000.00, 'TRANSFERENCIA_BANCARIA', 'PENDIENTE', 'PAY-2024-003', '2024-08-22 09:45:00', 'Pago tarifa registro sanitario - Aceite de Oliva'),
(4, 750000.00, 'TARJETA_DEBITO', 'RECHAZADO', 'PAY-2024-004', '2024-08-23 16:20:00', 'Pago tarifa renovación registro - Producto rechazado')
ON DUPLICATE KEY UPDATE id=id;


-- Insertar solicitudes de ejemplo
INSERT INTO solicitud (id, empresa_id, producto_id, tipo_tramite, estado, numero_radicado, fecha_radicacion, observaciones, pago_id) VALUES
(1, 1001, 1, 'REGISTRO', 'RADICADA', 'INV-1724159400000', '2024-08-20 11:30:00', 'Solicitud de registro sanitario para yogurt natural', 1),
(2, 1002, 2, 'REGISTRO', 'PENDIENTE', NULL, NULL, 'Solicitud en preparación para mermelada de fresa', 2)
ON DUPLICATE KEY UPDATE id=id;

-- Insertar documentos de ejemplo para la solicitud radicada
INSERT INTO documento (id, nombre_archivo, tipo_documento, ruta_archivo, tamanio_archivo, tipo_mime, fecha_carga, solicitud_id, obligatorio) VALUES
-- Documentos para solicitud ID 1 (Yogurt Natural - Radicada)
(1, 'certificado_constitucion_lacteos_valle.pdf', 'CERTIFICADO_CONSTITUCION', '/uploads/docs/cert_const_001.pdf', 2048576, 'application/pdf', '2024-08-20 09:00:00', 1, true),
(2, 'rut_lacteos_valle.pdf', 'RUT', '/uploads/docs/rut_001.pdf', 1024000, 'application/pdf', '2024-08-20 09:15:00', 1, true),
(3, 'concepto_sanitario_planta.pdf', 'CONCEPTO_SANITARIO', '/uploads/docs/concepto_san_001.pdf', 3072000, 'application/pdf', '2024-08-20 09:30:00', 1, true),
(4, 'ficha_tecnica_yogurt.pdf', 'FICHA_TECNICA', '/uploads/docs/ficha_tec_001.pdf', 1536000, 'application/pdf', '2024-08-20 09:45:00', 1, true),
(5, 'etiqueta_yogurt_natural.pdf', 'ETIQUETA', '/uploads/docs/etiqueta_001.pdf', 2048000, 'application/pdf', '2024-08-20 10:00:00', 1, true),
(6, 'analisis_microbiologico_yogurt.pdf', 'ANALISIS_MICROBIOLOGICO', '/uploads/docs/micro_001.pdf', 1792000, 'application/pdf', '2024-08-20 10:15:00', 1, true),
(7, 'certificado_bpm_lacteos.pdf', 'CERTIFICADO_BPM', '/uploads/docs/bpm_001.pdf', 2560000, 'application/pdf', '2024-08-20 10:30:00', 1, true),

-- Documentos para solicitud ID 2 (Mermelada - Pendiente) - documentos incompletos para testing
(8, 'certificado_constitucion_dulces.pdf', 'CERTIFICADO_CONSTITUCION', '/uploads/docs/cert_const_002.pdf', 1856000, 'application/pdf', '2024-08-21 10:00:00', 2, true),
(9, 'rut_dulces_tradicionales.pdf', 'RUT', '/uploads/docs/rut_002.pdf', 768000, 'application/pdf', '2024-08-21 10:15:00', 2, true),
(10, 'ficha_tecnica_mermelada.pdf', 'FICHA_TECNICA', '/uploads/docs/ficha_tec_002.pdf', 1280000, 'application/pdf', '2024-08-21 10:30:00', 2, true)
-- Nota: Faltan documentos obligatorios para la solicitud 2 (concepto sanitario, etiqueta, análisis, BPM)
ON DUPLICATE KEY UPDATE id=id;

-- Insertar documentos independientes para testing (sin solicitud asignada)
INSERT INTO documento (id, nombre_archivo, tipo_documento, ruta_archivo, tamanio_archivo, tipo_mime, fecha_carga, solicitud_id, obligatorio) VALUES
(11, 'concepto_sanitario_aceites.pdf', 'CONCEPTO_SANITARIO', '/uploads/docs/concepto_san_003.pdf', 2304000, 'application/pdf', '2024-08-22 08:00:00', NULL, true),
(12, 'etiqueta_aceite_oliva.pdf', 'ETIQUETA', '/uploads/docs/etiqueta_003.pdf', 1920000, 'application/pdf', '2024-08-22 08:15:00', NULL, true),
(13, 'analisis_fisicoquimico_aceite.pdf', 'ANALISIS_FISICOQUIMICO', '/uploads/docs/fisico_003.pdf', 1664000, 'application/pdf', '2024-08-22 08:30:00', NULL, true),
(14, 'certificado_bpm_gourmet.pdf', 'CERTIFICADO_BPM', '/uploads/docs/bpm_003.pdf', 2880000, 'application/pdf', '2024-08-22 08:45:00', NULL, true)
ON DUPLICATE KEY UPDATE id=id;
