-- Datos iniciales para plantillas de documentos dinámicos
-- Basado en requisitos oficiales de INVIMA para registros sanitarios de alimentos

-- Insertar plantillas de documentos según formatos oficiales INVIMA

-- 1. Formulario de Solicitud de Registro Sanitario (ASS-RSA-FM099)
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'FORMULARIO_SOLICITUD',
    'Formulario de Solicitud de Registro Sanitario',
    'Formulario oficial de solicitud de registro sanitario según formato ASS-RSA-FM099 de INVIMA',
    '[
        {"key":"numero_solicitud","label":"Número de solicitud","type":"text","required":true,"maxLength":50},
        {"key":"tipo_tramite","label":"Tipo de trámite","type":"select","required":true,"options":["REGISTRO","RENOVACION","MODIFICACION"]},
        {"key":"solicitante_nombre","label":"Nombre del solicitante","type":"text","required":true,"maxLength":200},
        {"key":"solicitante_nit","label":"NIT del solicitante","type":"text","required":true,"maxLength":20},
        {"key":"solicitante_direccion","label":"Dirección del solicitante","type":"text","required":true,"maxLength":300},
        {"key":"solicitante_telefono","label":"Teléfono","type":"tel","required":true},
        {"key":"solicitante_email","label":"Correo electrónico","type":"email","required":true},
        {"key":"representante_legal","label":"Representante legal","type":"text","required":true,"maxLength":200},
        {"key":"apoderado","label":"Apoderado (si aplica)","type":"text","required":false,"maxLength":200},
        {"key":"producto_nombre","label":"Nombre del producto","type":"text","required":true,"maxLength":200},
        {"key":"categoria_riesgo","label":"Categoría de riesgo","type":"select","required":true,"options":["ALTO","MEDIO","BAJO"]},
        {"key":"tipo_producto","label":"Tipo de producto","type":"select","required":true,"options":["ALIMENTO","BEBIDA","SUPLEMENTO","OTROS"]},
        {"key":"uso_previsto","label":"Uso previsto","type":"textarea","required":true},
        {"key":"pais_origen","label":"País de origen","type":"text","required":true,"maxLength":100}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":10485760,"multipleAllowed":false}',
    1, true, true, 1, null, 'SYSTEM'
);

-- 2. Ficha Técnica del Producto
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'FICHA_TECNICA',
    'Ficha Técnica del Producto',
    'Ficha técnica detallada del producto según requisitos INVIMA para alimentos',
    '[
        {"key":"nombre_comercial","label":"Nombre comercial","type":"text","required":true,"maxLength":200},
        {"key":"nombre_tecnico","label":"Nombre técnico/INCI","type":"text","required":true,"maxLength":300},
        {"key":"descripcion_producto","label":"Descripción del producto","type":"textarea","required":true},
        {"key":"composicion_cualitativa","label":"Composición cualitativa","type":"textarea","required":true},
        {"key":"composicion_cuantitativa","label":"Composición cuantitativa","type":"table","required":true,"columns":["ingrediente","porcentaje","unidad"]},
        {"key":"proceso_fabricacion","label":"Proceso de fabricación","type":"textarea","required":true},
        {"key":"vida_util","label":"Vida útil declarada (días)","type":"number","required":true,"min":1},
        {"key":"condiciones_almacenamiento","label":"Condiciones de almacenamiento","type":"textarea","required":true},
        {"key":"presentacion_comercial","label":"Presentación comercial","type":"text","required":true,"maxLength":200},
        {"key":"especificaciones_fisicoquimicas","label":"Especificaciones fisicoquímicas","type":"table","required":true,"columns":["parametro","unidad","limite_min","limite_max"]},
        {"key":"especificaciones_microbiologicas","label":"Especificaciones microbiológicas","type":"table","required":true,"columns":["microorganismo","limite","unidad","metodo"]},
        {"key":"pais_origen","label":"País de origen","type":"text","required":true,"maxLength":100}
    ]',
    '{"allowedMime":["application/pdf","image/jpeg","image/png"],"maxSize":15728640,"multipleAllowed":true}',
    1, true, true, 2, null, 'SYSTEM'
);

-- 3. Etiqueta/Rotulado
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'ETIQUETA',
    'Etiqueta y Rotulado',
    'Arte final de etiqueta con información nutricional y advertencias según normativa INVIMA',
    '[
        {"key":"nombre_comercial","label":"Nombre comercial","type":"text","required":true,"maxLength":200},
        {"key":"marca","label":"Marca","type":"text","required":true,"maxLength":100},
        {"key":"informacion_nutricional","label":"Información nutricional","type":"table","required":true,"columns":["nutriente","cantidad","porcentaje_vd"]},
        {"key":"declaraciones_nutricionales","label":"Declaraciones nutricionales","type":"textarea","required":false},
        {"key":"declaraciones_saludables","label":"Declaraciones de propiedades saludables","type":"textarea","required":false},
        {"key":"advertencias","label":"Advertencias obligatorias","type":"textarea","required":false},
        {"key":"lote_info","label":"Información de lote","type":"text","required":true,"maxLength":100},
        {"key":"fecha_vencimiento_formato","label":"Formato fecha de vencimiento","type":"text","required":true,"maxLength":50},
        {"key":"fabricante_nombre","label":"Nombre del fabricante","type":"text","required":true,"maxLength":200},
        {"key":"fabricante_direccion","label":"Dirección del fabricante","type":"text","required":true,"maxLength":300},
        {"key":"importador_info","label":"Información del importador (si aplica)","type":"text","required":false,"maxLength":300},
        {"key":"arte_final","label":"Arte final de etiqueta","type":"file","required":true}
    ]',
    '{"allowedMime":["application/pdf","image/jpeg","image/png","application/zip"],"maxSize":20971520,"multipleAllowed":true}',
    1, true, true, 3, null, 'SYSTEM'
);

-- 4. Certificado de Análisis (COA)
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'CERTIFICADO_ANALISIS',
    'Certificado de Análisis (COA)',
    'Certificado de análisis emitido por laboratorio acreditado',
    '[
        {"key":"laboratorio_nombre","label":"Nombre del laboratorio","type":"text","required":true,"maxLength":200},
        {"key":"laboratorio_acreditacion","label":"Número de acreditación","type":"text","required":true,"maxLength":100},
        {"key":"muestra_lote","label":"Lote analizado","type":"text","required":true,"maxLength":100},
        {"key":"fecha_muestreo","label":"Fecha de muestreo","type":"date","required":true},
        {"key":"fecha_analisis","label":"Fecha de análisis","type":"date","required":true},
        {"key":"parametros_evaluados","label":"Parámetros evaluados","type":"table","required":true,"columns":["parametro","unidad","limite","resultado","cumple"]},
        {"key":"metodos_analisis","label":"Métodos de análisis utilizados","type":"textarea","required":true},
        {"key":"responsable_tecnico","label":"Responsable técnico","type":"text","required":true,"maxLength":200},
        {"key":"observaciones","label":"Observaciones","type":"textarea","required":false}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":10485760,"multipleAllowed":false}',
    1, true, true, 4, 'ALTO', 'SYSTEM'
);

-- 5. Certificado de Buenas Prácticas de Manufactura (BPM)
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'CERTIFICADO_BPM',
    'Certificado de Buenas Prácticas de Manufactura',
    'Certificado BPM de la planta de producción emitido por autoridad competente',
    '[
        {"key":"numero_certificado","label":"Número del certificado","type":"text","required":true,"maxLength":100},
        {"key":"entidad_emisora","label":"Entidad emisora","type":"text","required":true,"maxLength":200},
        {"key":"planta_nombre","label":"Nombre de la planta","type":"text","required":true,"maxLength":200},
        {"key":"planta_direccion","label":"Dirección de la planta","type":"text","required":true,"maxLength":300},
        {"key":"fecha_expedicion","label":"Fecha de expedición","type":"date","required":true},
        {"key":"fecha_vencimiento","label":"Fecha de vencimiento","type":"date","required":true},
        {"key":"alcance_certificacion","label":"Alcance de la certificación","type":"textarea","required":true},
        {"key":"productos_certificados","label":"Productos certificados","type":"textarea","required":true},
        {"key":"observaciones","label":"Observaciones","type":"textarea","required":false}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":10485760,"multipleAllowed":false}',
    1, true, true, 5, null, 'SYSTEM'
);

-- 6. Certificado de Existencia y Representación Legal
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'CERTIFICADO_EXISTENCIA',
    'Certificado de Existencia y Representación Legal',
    'Certificado de existencia y representación legal o matrícula mercantil vigente',
    '[
        {"key":"numero_certificado","label":"Número del certificado","type":"text","required":true,"maxLength":100},
        {"key":"camara_comercio","label":"Cámara de comercio emisora","type":"text","required":true,"maxLength":200},
        {"key":"fecha_expedicion","label":"Fecha de expedición","type":"date","required":true},
        {"key":"vigencia","label":"Vigencia (días)","type":"number","required":true,"min":1},
        {"key":"razon_social","label":"Razón social","type":"text","required":true,"maxLength":300},
        {"key":"nit","label":"NIT","type":"text","required":true,"maxLength":20},
        {"key":"representante_legal","label":"Representante legal","type":"text","required":true,"maxLength":200},
        {"key":"objeto_social","label":"Objeto social","type":"textarea","required":true},
        {"key":"capital_autorizado","label":"Capital autorizado","type":"number","required":false},
        {"key":"capital_suscrito","label":"Capital suscrito","type":"number","required":false}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":5242880,"multipleAllowed":false}',
    1, true, true, 6, null, 'SYSTEM'
);

-- 7. Poder o Carta de Representación (si aplica)
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'PODER_REPRESENTACION',
    'Poder o Carta de Representación',
    'Poder otorgado al apoderado para realizar el trámite ante INVIMA',
    '[
        {"key":"poderdante_nombre","label":"Nombre del poderdante","type":"text","required":true,"maxLength":200},
        {"key":"poderdante_identificacion","label":"Identificación del poderdante","type":"text","required":true,"maxLength":50},
        {"key":"apoderado_nombre","label":"Nombre del apoderado","type":"text","required":true,"maxLength":200},
        {"key":"apoderado_identificacion","label":"Identificación del apoderado","type":"text","required":true,"maxLength":50},
        {"key":"objeto_poder","label":"Objeto del poder","type":"textarea","required":true},
        {"key":"fecha_expedicion","label":"Fecha de expedición","type":"date","required":true},
        {"key":"notaria","label":"Notaría donde se otorgó","type":"text","required":true,"maxLength":200},
        {"key":"numero_escritura","label":"Número de escritura","type":"text","required":false,"maxLength":100}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":5242880,"multipleAllowed":false}',
    1, true, false, 7, null, 'SYSTEM'
);

-- 8. Comprobante de Pago INVIMA
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'COMPROBANTE_PAGO',
    'Comprobante de Pago de Tasas INVIMA',
    'Recibo de pago de tasas oficial de INVIMA para el trámite',
    '[
        {"key":"numero_recibo","label":"Número de recibo","type":"text","required":true,"maxLength":100},
        {"key":"entidad_financiera","label":"Entidad financiera","type":"text","required":true,"maxLength":200},
        {"key":"referencia_pago","label":"Referencia de pago","type":"text","required":true,"maxLength":100},
        {"key":"valor_pagado","label":"Valor pagado","type":"number","required":true,"min":0},
        {"key":"fecha_pago","label":"Fecha de pago","type":"date","required":true},
        {"key":"tramite_asociado","label":"Trámite asociado","type":"text","required":true,"maxLength":200},
        {"key":"concepto_pago","label":"Concepto del pago","type":"text","required":true,"maxLength":300}
    ]',
    '{"allowedMime":["application/pdf","image/jpeg","image/png"],"maxSize":5242880,"multipleAllowed":false}',
    1, true, true, 8, null, 'SYSTEM'
);

-- 9. Certificado de Venta Libre (productos importados)
INSERT INTO document_template (code, name, description, fields_definition, file_rules, version, active, required, display_order, categoria_riesgo, created_by)
VALUES (
    'CERTIFICADO_VENTA_LIBRE',
    'Certificado de Venta Libre',
    'Certificado de venta libre emitido por autoridad competente del país de origen (para productos importados)',
    '[
        {"key":"pais_emisor","label":"País emisor","type":"text","required":true,"maxLength":100},
        {"key":"autoridad_competente","label":"Autoridad competente emisora","type":"text","required":true,"maxLength":200},
        {"key":"numero_certificado","label":"Número de certificado","type":"text","required":true,"maxLength":100},
        {"key":"fecha_emision","label":"Fecha de emisión","type":"date","required":true},
        {"key":"vigencia","label":"Vigencia (meses)","type":"number","required":true,"min":1},
        {"key":"producto_certificado","label":"Producto certificado","type":"text","required":true,"maxLength":300},
        {"key":"fabricante","label":"Fabricante","type":"text","required":true,"maxLength":200},
        {"key":"observaciones","label":"Observaciones","type":"textarea","required":false}
    ]',
    '{"allowedMime":["application/pdf"],"maxSize":5242880,"multipleAllowed":false}',
    1, true, false, 9, null, 'SYSTEM'
);

-- Insertar tipos de trámite aplicables para cada plantilla
-- Formulario de Solicitud - aplica a todos los tipos
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'FORMULARIO_SOLICITUD';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'RENOVACION' FROM document_template WHERE code = 'FORMULARIO_SOLICITUD';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'FORMULARIO_SOLICITUD';

-- Ficha Técnica - aplica a registro y modificación
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'FICHA_TECNICA';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'FICHA_TECNICA';

-- Etiqueta - aplica a registro y modificación
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'ETIQUETA';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'ETIQUETA';

-- Certificado de Análisis - aplica principalmente a registro
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'CERTIFICADO_ANALISIS';

-- BPM - aplica a registro
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'CERTIFICADO_BPM';

-- Certificado de Existencia - aplica a todos
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'CERTIFICADO_EXISTENCIA';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'RENOVACION' FROM document_template WHERE code = 'CERTIFICADO_EXISTENCIA';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'CERTIFICADO_EXISTENCIA';

-- Poder - aplica a todos (opcional)
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'PODER_REPRESENTACION';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'RENOVACION' FROM document_template WHERE code = 'PODER_REPRESENTACION';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'PODER_REPRESENTACION';

-- Comprobante de pago - aplica a todos
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'COMPROBANTE_PAGO';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'RENOVACION' FROM document_template WHERE code = 'COMPROBANTE_PAGO';
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'MODIFICACION' FROM document_template WHERE code = 'COMPROBANTE_PAGO';

-- Certificado de Venta Libre - solo para registro de productos importados
INSERT INTO template_tramite_types (template_id, tramite_type)
SELECT id, 'REGISTRO' FROM document_template WHERE code = 'CERTIFICADO_VENTA_LIBRE';
