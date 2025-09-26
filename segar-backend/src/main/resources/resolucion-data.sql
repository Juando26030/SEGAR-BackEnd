-- Scripts SQL para tablas del módulo de resolución
-- Se ejecutarán automáticamente con la configuración spring.jpa.hibernate.ddl-auto=create-drop

-- Datos de prueba para resoluciones
INSERT INTO resoluciones (id, numero_resolucion, fecha_emision, autoridad, estado, observaciones, tramite_id, fecha_notificacion) VALUES
(1, '2024-INVIMA-0001', '2024-08-25 10:00:00', 'INVIMA', 'APROBADA', 'Solicitud aprobada después de revisión técnica exitosa', 1, '2024-08-25 10:30:00'),
(2, '2024-INVIMA-0002', '2024-08-26 14:30:00', 'INVIMA', 'EN_REVISION', 'Resolución en proceso de revisión por comité técnico', 2, NULL)
ON DUPLICATE KEY UPDATE id=id;

-- Datos de prueba para registros sanitarios
INSERT INTO registros_sanitarios (id, numero_registro, fecha_expedicion, fecha_vencimiento, producto_id, empresa_id, estado, resolucion_id) VALUES
(1, 'RSAA21M-20240001', '2024-08-25 11:00:00', '2029-08-25 11:00:00', 1, 1001, 'VIGENTE', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Datos de prueba para historial de trámites
INSERT INTO historial_tramites (id, tramite_id, fecha, accion, descripcion, usuario, estado) VALUES
(1, 1, '2024-08-20 11:30:00', 'SOLICITUD_RADICADA', 'Solicitud radicada con número INV-20240820-000001', 'Sistema', 'RADICADA'),
(2, 1, '2024-08-22 09:15:00', 'REVISION_INICIADA', 'Revisión técnica iniciada por especialista INVIMA', 'Juan Pérez', 'EN_REVISION'),
(3, 1, '2024-08-25 10:00:00', 'RESOLUCION_APROBADA', 'Resolución 2024-INVIMA-0001 aprobada', 'INVIMA', 'APROBADA'),
(4, 1, '2024-08-25 11:00:00', 'REGISTRO_GENERADO', 'Registro sanitario RSAA21M-20240001 generado', 'Sistema', 'FINALIZADO'),
(5, 2, '2024-08-21 14:15:00', 'SOLICITUD_RADICADA', 'Solicitud radicada para mermelada de fresa', 'Sistema', 'RADICADA'),
(6, 2, '2024-08-26 14:30:00', 'REVISION_INICIADA', 'Revisión técnica iniciada', 'María González', 'EN_REVISION')
ON DUPLICATE KEY UPDATE id=id;
