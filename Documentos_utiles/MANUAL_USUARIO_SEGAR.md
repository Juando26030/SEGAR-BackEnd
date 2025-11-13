# 📖 Manual de Usuario - Sistema SEGAR

**Software Especializado para la Gestión de Asuntos Regulatorios**

---

## 📋 Tabla de Contenidos

1. [Introducción al Sistema](#introducción-al-sistema)
2. [Acceso al Sistema](#acceso-al-sistema)
3. [Interfaz Principal](#interfaz-principal)
4. [Dashboard Empresarial](#dashboard-empresarial)
5. [Gestión de Trámites](#gestión-de-trámites)
6. [Gestión de Usuarios](#gestión-de-usuarios)
7. [Calendario y Eventos](#calendario-y-eventos)
8. [Sistema de Correos](#sistema-de-correos)
9. [Búsqueda Global](#búsqueda-global)
10. [Configuración del Sistema](#configuración-del-sistema)
11. [Perfil de Usuario](#perfil-de-usuario)
12. [Solución de Problemas](#solución-de-problemas)
13. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## 🚀 Introducción al Sistema

### ¿Qué es SEGAR?

SEGAR (Software Especializado para la Gestión de Asuntos Regulatorios) es una aplicación web diseñada para automatizar la gestión de trámites regulatorios en el sector de alimentos procesados en Colombia, específicamente ante el INVIMA.

### ¿Para quién está diseñado?

- **Micro, Pequeñas y Medianas Empresas (MIPYMES)** del sector de alimentos procesados
- **Empresas** que necesitan gestionar registros sanitarios
- **Usuarios** que requieren seguimiento en tiempo real de trámites regulatorios

### Características principales:

- ✅ **Digitalización** del proceso de registros sanitarios
- ✅ **Almacenamiento seguro** de documentación
- ✅ **Alertas automáticas** de vencimientos
- ✅ **Seguimiento en tiempo real** del estado de trámites
- ✅ **Dashboard centralizado** para monitoreo
- ✅ **Gestión de usuarios** con diferentes roles

---

## 🔐 Acceso al Sistema

### 1. Acceso Inicial

1. **Abra su navegador web** (Chrome, Firefox, Safari, Edge)
2. **Navegue a la URL** proporcionada por su administrador
3. **Será redirigido** automáticamente a la página de inicio de sesión

### 2. Inicio de Sesión

#### Pantalla de Login
```
┌─────────────────────────────────────┐
│            🔑 SEGAR Login            │
├─────────────────────────────────────┤
│ Usuario: [________________]          │
│ Contraseña: [________________]       │
│                                     │
│ [    Iniciar Sesión    ]            │
│                                     │
│ ¿Olvidó su contraseña?              │
│ [Recuperar Contraseña]              │
└─────────────────────────────────────┘
```

#### Pasos para iniciar sesión:

1. **Ingrese su nombre de usuario** en el campo correspondiente
2. **Ingrese su contraseña** en el campo de contraseña
3. **Haga clic en "Iniciar Sesión"**
4. **Espere** a que el sistema valide sus credenciales

### 3. Recuperación de Contraseña

Si olvidó su contraseña:

1. **Haga clic en "Recuperar Contraseña"**
2. **Ingrese su email** registrado en el sistema
3. **Revise su correo electrónico** para las instrucciones
4. **Siga los pasos** para restablecer su contraseña

### 4. Roles de Usuario

El sistema maneja tres tipos de roles:

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **Administrador** | Acceso completo al sistema | ✅ Todas las funcionalidades<br>✅ Gestión de usuarios<br>✅ Configuración del sistema |
| **Empleado** | Usuario operativo | ✅ Gestión de trámites<br>✅ Documentos<br>✅ Calendario |
| **Supervisor** | Revisión y aprobación | ✅ Revisión de trámites<br>✅ Aprobaciones<br>✅ Reportes |

---

## 🖥️ Interfaz Principal

### Estructura de la Interfaz

```
┌─────────────────────────────────────────────────────────────────┐
│ 🔵 SEGAR                    👤 Usuario Actual    🔔 ⚙️ 🚪     │
├─────────────────────────────────────────────────────────────────┤
│ 📋 Panel │ 📊 Dashboard │ 📅 Calendario │ 📧 Correos │ 👥 Usuarios │
│ Principal │              │               │            │ (Admin)     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                    CONTENIDO PRINCIPAL                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Elementos de la Interfaz

#### 1. **Barra Superior**
- **Logo de SEGAR**: Identificación del sistema
- **Información del usuario**: Nombre y rol actual
- **Notificaciones**: Alertas y mensajes del sistema
- **Configuración**: Acceso rápido a configuraciones
- **Cerrar Sesión**: Botón para salir del sistema

#### 2. **Menú Lateral (Sidebar)**
- **Panel Principal**: Página de inicio
- **Dashboard**: Métricas y estadísticas
- **Calendario**: Eventos y fechas importantes
- **Correos**: Sistema de comunicación
- **Nuevo Trámite**: Crear nuevos trámites
- **Usuarios**: Gestión de usuarios (solo Admin)
- **Búsqueda Global**: Buscar en todo el sistema
- **Configuración**: Ajustes del sistema
- **Ayuda**: Documentación y soporte

#### 3. **Área de Contenido**
- **Contenido dinámico** según la sección seleccionada
- **Navegación de migas de pan** (breadcrumbs)
- **Botones de acción** contextuales

---

## 📊 Dashboard Empresarial

### ¿Qué es el Dashboard?

El Dashboard es el centro de control del sistema donde puede visualizar todas las métricas importantes de su empresa de manera gráfica y en tiempo real.

### Acceso al Dashboard

1. **Haga clic en "Dashboard"** en el menú lateral
2. **El sistema cargará** las métricas más recientes
3. **Use el botón "Actualizar"** para refrescar los datos

### Métricas Principales

#### 1. **Finanzas del Mes**
```
💰 Finanzas del Mes
┌─────────────────────────────┐
│ Utilidad Neta: $2,450,000   │
│                             │
│ Ingresos: $5,200,000        │
│ Gastos: $2,750,000          │
│                             │
│ Crecimiento: +12% ↗️        │
└─────────────────────────────┘
```

#### 2. **Estado de Trámites**
```
📋 Estado de Trámites
┌─────────────────────────────┐
│ Total Trámites: 45          │
│                             │
│ ✅ Completados: 28 (62%)    │
│ ████████████████░░░░        │
│                             │
│ 🔄 En Proceso: 12 (27%)     │
│ ████████░░░░░░░░░░░░        │
│                             │
│ ⏳ Pendientes: 5 (11%)      │
│ ███░░░░░░░░░░░░░░░░░        │
└─────────────────────────────┘
```

#### 3. **Registros Sanitarios**
```
🏥 Registros Sanitarios
┌─────────────────────────────┐
│ Total Registros: 23         │
│                             │
│ ✅ Vigentes: 18             │
│ ⚠️ Por Vencer: 3            │
│ ❌ Vencidos: 2              │
└─────────────────────────────┘
```

### Gráficos y Visualizaciones

#### 1. **Trámites por Mes**
- **Gráfico de barras** mostrando la evolución mensual
- **Selector de año** para cambiar el período
- **Botones de navegación** rápida entre años
- **Estadísticas** del año seleccionado

#### 2. **Distribución de Trámites**
- **Gráfico circular (donut)** con porcentajes
- **Leyenda** con colores diferenciados
- **Actualización automática** de datos

### Eventos Próximos

El dashboard muestra los eventos más importantes:

```
📅 Eventos Próximos
┌─────────────────────────────┐
│ 🚨 Vencimiento Registro     │
│ Producto A - 15 días        │
│                             │
│ 📋 Revisión Documentos      │
│ Proyecto B - 7 días         │
│                             │
│ 🔄 Renovación Registro      │
│ Producto C - 30 días        │
└─────────────────────────────┘
```

### Accesos Rápidos

Botones de acceso directo a funciones importantes:

- **➕ Nuevo Trámite**: Crear un nuevo trámite
- **📄 Documentos**: Gestionar documentos
- **📅 Calendario**: Ver calendario completo
- **⚙️ Configuración**: Ajustes del sistema

---

## 📝 Gestión de Trámites

### ¿Qué son los Trámites?

Los trámites son procesos regulatorios que debe realizar ante el INVIMA para obtener, renovar o modificar registros sanitarios de sus productos.

### Tipos de Trámites Disponibles

#### 1. **Registro Sanitario** 🆕
- **Propósito**: Obtener autorización para fabricar, importar o comercializar un producto nuevo
- **Duración**: 90-120 días hábiles
- **Documentos**: Ficha técnica, etiquetas, certificados

#### 2. **Renovación** 🔄
- **Propósito**: Extender la vigencia de un registro sanitario existente
- **Duración**: 60-90 días hábiles
- **Documentos**: Registro actual, actualizaciones

#### 3. **Modificación** ✏️
- **Propósito**: Realizar cambios en un registro sanitario aprobado
- **Duración**: 45-60 días hábiles
- **Documentos**: Justificación de cambios

### Crear un Nuevo Trámite

#### Paso 1: Acceso
1. **Haga clic en "Nuevo Trámite"** en el menú lateral
2. **Revise la información importante** sobre requerimientos
3. **Seleccione el tipo de trámite** que desea crear

#### Paso 2: Selección del Tipo
```
┌─────────────────────────────────────────────────────────────────┐
│                        Nuevo Trámite                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📋 Registro Sanitario    🔄 Renovación    ✏️ Modificación      │
│                                                                 │
│  [    Seleccionar    ]   [  Seleccionar  ]  [  Seleccionar   ] │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Paso 3: Información Preparatoria

**⚠️ IMPORTANTE**: Antes de iniciar cualquier trámite, debe revisar:

1. **📋 Evaluación Inicial**
   - Documentación requerida
   - Tiempos estimados
   - Costos asociados

2. **💻 Registro en Plataforma**
   - Registro en plataforma INVIMA
   - Configuración de usuario
   - Verificación de datos

#### Paso 4: Proceso del Trámite

Una vez seleccionado el tipo, seguirá un proceso guiado:

##### Para Registro Sanitario:
1. **Información del Producto**
   - Nombre comercial
   - Categoría
   - Descripción

2. **Información de la Empresa**
   - Datos del fabricante
   - Ubicación
   - Certificaciones

3. **Documentación Técnica**
   - Ficha técnica
   - Etiquetas
   - Certificados de análisis

4. **Información Adicional**
   - Comentarios
   - Observaciones
   - Documentos complementarios

5. **Revisión y Envío**
   - Verificación de datos
   - Confirmación
   - Envío al INVIMA

### Seguimiento de Trámites

#### Estados del Trámite

| Estado | Descripción | Acciones Disponibles |
|--------|-------------|---------------------|
| **📝 Borrador** | En proceso de creación | Editar, completar, eliminar |
| **📤 Enviado** | Enviado al INVIMA | Ver detalles, descargar documentos |
| **🔍 En Revisión** | INVIMA está revisando | Esperar, consultar estado |
| **📋 Documentos Faltantes** | Requiere documentación adicional | Subir documentos, responder |
| **✅ Aprobado** | Trámite aprobado | Descargar resolución, renovar |
| **❌ Rechazado** | Trámite rechazado | Ver observaciones, corregir |

#### Timeline del Trámite

```
┌─────────────────────────────────────────────────────────────────┐
│                    Timeline del Trámite                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ 📝 Creado    📤 Enviado    🔍 Revisión    ✅ Aprobado          │
│   15/01/24     16/01/24      20/01/24      15/03/24            │
│                                                                 │
│ ──────●──────────●────────────●──────────────●                 │
│                                                                 │
│ Estado actual: 🔍 En Revisión                                   │
│ Tiempo transcurrido: 45 días                                   │
│ Tiempo estimado restante: 15 días                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Gestión de Documentos

#### Subir Documentos
1. **Haga clic en "Gestionar Documentos"** desde el dashboard
2. **Seleccione el trámite** correspondiente
3. **Haga clic en "Subir Documento"**
4. **Seleccione el archivo** desde su computador
5. **Complete la información** del documento
6. **Haga clic en "Guardar"**

#### Tipos de Documentos Soportados
- **PDF**: Documentos principales
- **DOC/DOCX**: Documentos editables
- **XLS/XLSX**: Hojas de cálculo
- **JPG/PNG**: Imágenes y fotos
- **ZIP**: Archivos comprimidos

#### Control de Versiones
- **Historial** de versiones
- **Comentarios** en cada versión
- **Fechas** de modificación
- **Usuario** que realizó el cambio

---

## 👥 Gestión de Usuarios

> **⚠️ IMPORTANTE**: Esta funcionalidad está disponible **SOLO para usuarios con rol de Administrador**.

### Acceso a Gestión de Usuarios

1. **Verifique que tiene rol de Administrador**
2. **Haga clic en "Usuarios"** en el menú lateral
3. **El sistema validará** sus permisos automáticamente

### Funcionalidades Disponibles

#### 1. **Ver Lista de Usuarios**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Gestión de Usuarios                          │
├─────────────────────────────────────────────────────────────────┤
│ [➕ Agregar Usuario]  [🔄 Actualizar]                           │
├─────────────────────────────────────────────────────────────────┤
│ ID │ Usuario │ Nombre Completo │ Email │ Rol │ Estado │ Acciones │
├─────────────────────────────────────────────────────────────────┤
│ 1  │ admin   │ Admin Sistema   │ ...   │ Admin│ Activo │ ✏️🔑🗑️  │
│ 2  │ empleado│ Juan Pérez      │ ...   │ Emp. │ Activo │ ✏️🔑🗑️  │
└─────────────────────────────────────────────────────────────────┘
```

#### 2. **Crear Nuevo Usuario**

**Paso a paso:**

1. **Haga clic en "Agregar Usuario"**
2. **Complete los campos obligatorios**:
   - **Nombre de usuario**: Único en el sistema
   - **Email**: Correo electrónico válido
   - **Nombre**: Nombre del usuario
   - **Apellido**: Apellido del usuario
   - **Contraseña**: Mínimo 8 caracteres
   - **Rol**: Admin, Empleado o Supervisor

3. **Complete la información adicional** (opcional):
   - Tipo de documento
   - Número de documento
   - Fecha de nacimiento
   - Género
   - Teléfonos
   - Dirección
   - ID de empleado

4. **Seleccione el estado**: Activo/Inactivo
5. **Haga clic en "Crear Usuario"**

#### 3. **Editar Usuario Existente**

1. **Haga clic en el ícono de editar** (✏️) del usuario
2. **Modifique los campos** necesarios
3. **Haga clic en "Actualizar Usuario"**

> **Nota**: El nombre de usuario no se puede cambiar una vez creado.

#### 4. **Cambiar Contraseña de Usuario**

1. **Haga clic en el ícono de llave** (🔑) del usuario
2. **Ingrese la nueva contraseña**
3. **Seleccione si es temporal** (el usuario debe cambiarla)
4. **Haga clic en "Cambiar Contraseña"**

#### 5. **Activar/Desactivar Usuario**

1. **Haga clic en el ícono de toggle** del usuario
2. **Confirme la acción** en el diálogo
3. **El sistema actualizará** el estado del usuario

#### 6. **Eliminar Usuario**

> **⚠️ ADVERTENCIA**: Esta acción es **IRREVERSIBLE**.

1. **Haga clic en el ícono de eliminar** (🗑️) del usuario
2. **Confirme la eliminación** en el diálogo
3. **El usuario será eliminado** de Keycloak y la base de datos

### Sincronización con Keycloak

El sistema se sincroniza automáticamente con Keycloak:

- **Creación**: Usuario creado en ambos sistemas
- **Actualización**: Cambios sincronizados automáticamente
- **Eliminación**: Usuario eliminado de ambos sistemas
- **Estados**: Estados activo/inactivo sincronizados

### Información sobre Roles

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **Administrador** | Acceso completo | ✅ Todas las funcionalidades<br>✅ Gestión de usuarios<br>✅ Configuración del sistema |
| **Empleado** | Usuario operativo | ✅ Gestión de trámites<br>✅ Documentos<br>✅ Calendario<br>❌ Gestión de usuarios |
| **Supervisor** | Revisión y aprobación | ✅ Revisión de trámites<br>✅ Aprobaciones<br>✅ Reportes<br>❌ Gestión de usuarios |

---

## 📅 Calendario y Eventos

### ¿Qué es el Calendario?

El calendario le permite visualizar y gestionar todos los eventos importantes relacionados con sus trámites regulatorios.

### Acceso al Calendario

1. **Haga clic en "Calendario"** en el menú lateral
2. **El sistema mostrará** el calendario del mes actual
3. **Use las flechas** para navegar entre meses

### Vista del Calendario

```
┌─────────────────────────────────────────────────────────────────┐
│                    📅 Calendario SEGAR                          │
├─────────────────────────────────────────────────────────────────┤
│        ← Enero 2024 →                                           │
├─────────────────────────────────────────────────────────────────┤
│ Dom │ Lun │ Mar │ Mié │ Jue │ Vie │ Sáb │                      │
├─────────────────────────────────────────────────────────────────┤
│     │  1  │  2  │  3  │  4  │  5  │  6  │                      │
│  7  │  8  │  9  │ 10  │ 11  │ 12  │ 13  │                      │
│ 14  │ 15  │ 16  │ 17  │ 18  │ 19  │ 20  │                      │
│ 21  │ 22  │ 23  │ 24  │ 25  │ 26  │ 27  │                      │
│ 28  │ 29  │ 30  │ 31  │     │     │     │                      │
└─────────────────────────────────────────────────────────────────┘
```

### Tipos de Eventos

#### 1. **Vencimientos de Registros** 🚨
- **Color**: Rojo
- **Descripción**: Registros que están próximos a vencer
- **Acción**: Renovar antes de la fecha

#### 2. **Revisiones de Documentos** 📋
- **Color**: Azul
- **Descripción**: Fechas límite para entrega de documentos
- **Acción**: Preparar y enviar documentos

#### 3. **Auditorías** 🔍
- **Color**: Amarillo
- **Descripción**: Visitas programadas del INVIMA
- **Acción**: Preparar instalaciones y documentación

#### 4. **Reuniones** 👥
- **Color**: Verde
- **Descripción**: Reuniones internas o con autoridades
- **Acción**: Asistir y preparar agenda

### Gestión de Eventos

#### Ver Eventos del Día
1. **Haga clic en cualquier fecha** del calendario
2. **Se mostrará** la lista de eventos del día
3. **Haga clic en un evento** para ver detalles

#### Crear Nuevo Evento
1. **Haga clic en "Nuevo Evento"**
2. **Complete la información**:
   - Título del evento
   - Fecha y hora
   - Descripción
   - Tipo de evento
   - Prioridad
3. **Haga clic en "Guardar"**

#### Editar Evento
1. **Haga clic en el evento** que desea editar
2. **Modifique la información** necesaria
3. **Haga clic en "Actualizar"**

#### Eliminar Evento
1. **Haga clic en el evento** que desea eliminar
2. **Haga clic en "Eliminar"**
3. **Confirme la acción**

### Alertas y Notificaciones

#### Tipos de Alertas
- **🚨 Crítica**: Eventos que requieren acción inmediata
- **⚠️ Importante**: Eventos que requieren atención pronto
- **ℹ️ Informativa**: Recordatorios y actualizaciones

#### Configuración de Alertas
1. **Haga clic en "Configurar Alertas"**
2. **Seleccione los tipos** de eventos para alertar
3. **Configure los tiempos** de anticipación
4. **Seleccione los métodos** de notificación

---

## 📧 Sistema de Correos

### ¿Qué es el Sistema de Correos?

El sistema de correos integra la comunicación entre su empresa y las autoridades regulatorias, permitiendo un seguimiento completo de todas las comunicaciones oficiales.

### Acceso al Sistema de Correos

1. **Haga clic en "Correos"** en el menú lateral
2. **El sistema mostrará** su bandeja de entrada
3. **Use los filtros** para organizar los mensajes

### Estructura del Sistema de Correos

```
┌─────────────────────────────────────────────────────────────────┐
│                      📧 Sistema de Correos                      │
├─────────────────────────────────────────────────────────────────┤
│ [📥 Recibidos] [📤 Enviados] [📝 Borradores] [🗑️ Eliminados]    │
├─────────────────────────────────────────────────────────────────┤
│ Filtros: [Todos] [No leídos] [Importantes] [Adjuntos]           │
├─────────────────────────────────────────────────────────────────┤
│ 📧 Correo 1 - Asunto...                    📅 15/01/24    ⭐   │
│ 📧 Correo 2 - Asunto...                    📅 14/01/24        │
│ 📧 Correo 3 - Asunto...                    📅 13/01/24    ⭐   │
└─────────────────────────────────────────────────────────────────┘
```

### Funcionalidades Principales

#### 1. **Bandeja de Entrada**
- **Lista de correos** recibidos
- **Indicadores visuales**:
  - 📧 Correo normal
  - ⭐ Importante
  - 📎 Con adjuntos
  - 🔴 No leído

#### 2. **Leer Correo**
1. **Haga clic en el correo** que desea leer
2. **Se abrirá** el contenido completo
3. **Marque como leído** automáticamente
4. **Descargue adjuntos** si los hay

#### 3. **Enviar Correo**
1. **Haga clic en "Nuevo Correo"**
2. **Complete los campos**:
   - Para: Destinatario(s)
   - Asunto: Título del mensaje
   - Contenido: Mensaje principal
3. **Adjunte archivos** si es necesario
4. **Haga clic en "Enviar"**

#### 4. **Gestión de Adjuntos**
- **Subir archivos** desde su computador
- **Descargar adjuntos** de correos recibidos
- **Vista previa** de archivos compatibles
- **Tamaño máximo**: 25MB por archivo

### Sincronización Automática

El sistema se sincroniza automáticamente con:
- **Servidor de correo** de la empresa
- **Correos del INVIMA**
- **Notificaciones del sistema**

### Organización de Correos

#### Filtros Disponibles
- **Todos**: Todos los correos
- **No leídos**: Correos sin leer
- **Importantes**: Correos marcados como importantes
- **Con adjuntos**: Correos que contienen archivos
- **Por fecha**: Filtrar por rango de fechas

#### Acciones Rápidas
- **Marcar como leído/no leído**
- **Marcar como importante**
- **Archivar correo**
- **Eliminar correo**
- **Responder/Reenviar**

---

## 🔍 Búsqueda Global

### ¿Qué es la Búsqueda Global?

La búsqueda global le permite buscar información en todo el sistema: trámites, documentos, usuarios, correos y eventos.

### Acceso a la Búsqueda

1. **Haga clic en "Búsqueda Global"** en el menú lateral
2. **Ingrese su término de búsqueda** en el campo
3. **Seleccione el tipo** de contenido a buscar
4. **Haga clic en "Buscar"**

### Tipos de Búsqueda

#### 1. **Búsqueda General**
- **Busca en**: Todos los contenidos del sistema
- **Incluye**: Trámites, documentos, usuarios, correos
- **Resultados**: Lista completa con filtros

#### 2. **Búsqueda por Tipo**
- **Trámites**: Solo trámites y procesos
- **Documentos**: Solo archivos y documentos
- **Usuarios**: Solo información de usuarios
- **Correos**: Solo mensajes de correo

#### 3. **Búsqueda Avanzada**
```
┌─────────────────────────────────────────────────────────────────┐
│                    🔍 Búsqueda Avanzada                         │
├─────────────────────────────────────────────────────────────────┤
│ Término: [_____________________________]                        │
│                                                                 │
│ Tipo: [Todos ▼]                                                │
│ Fecha: [Desde] [Hasta]                                         │
│ Usuario: [________________]                                    │
│ Estado: [Todos ▼]                                              │
│                                                                 │
│ [🔍 Buscar] [🔄 Limpiar]                                       │
└─────────────────────────────────────────────────────────────────┘
```

### Resultados de Búsqueda

#### Visualización de Resultados
```
┌─────────────────────────────────────────────────────────────────┐
│                    Resultados de Búsqueda                       │
├─────────────────────────────────────────────────────────────────┤
│ Encontrados: 15 resultados para "registro sanitario"            │
├─────────────────────────────────────────────────────────────────┤
│ 📋 Trámite #12345 - Registro Sanitario Producto A              │
│    Estado: En Revisión | Fecha: 15/01/2024                    │
│                                                                 │
│ 📄 Documento - Ficha Técnica Producto A.pdf                    │
│    Tamaño: 2.5 MB | Fecha: 10/01/2024                        │
│                                                                 │
│ 📧 Correo - Respuesta INVIMA Trámite #12345                    │
│    De: INVIMA | Fecha: 12/01/2024                             │
└─────────────────────────────────────────────────────────────────┘
```

#### Acciones sobre Resultados
- **Ver detalles** del elemento
- **Descargar** documentos
- **Abrir** trámite o correo
- **Marcar como favorito**

### Filtros de Búsqueda

#### Filtros Disponibles
- **Por fecha**: Rango de fechas
- **Por usuario**: Usuario que creó el contenido
- **Por estado**: Estado del trámite o documento
- **Por tipo**: Tipo de archivo o proceso
- **Por tamaño**: Tamaño de archivos

#### Combinación de Filtros
Puede combinar múltiples filtros para obtener resultados más precisos:
1. **Seleccione el tipo** de contenido
2. **Defina el rango** de fechas
3. **Especifique el usuario** si es necesario
4. **Aplique filtros adicionales**
5. **Ejecute la búsqueda**

---

## ⚙️ Configuración del Sistema

### ¿Qué es la Configuración?

La configuración del sistema le permite personalizar el comportamiento de SEGAR según las necesidades de su empresa.

### Acceso a la Configuración

1. **Haga clic en "Configuración"** en el menú lateral
2. **Seleccione la categoría** que desea configurar
3. **Realice los cambios** necesarios
4. **Guarde la configuración**

### Categorías de Configuración

#### 1. **Configuración General**
- **Nombre de la empresa**
- **Logo corporativo**
- **Información de contacto**
- **Configuración regional**

#### 2. **Configuración de Trámites**
- **Tipos de trámites** habilitados
- **Plantillas** de documentos
- **Flujos de aprobación**
- **Tiempos estimados**

#### 3. **Configuración de Notificaciones**
- **Tipos de alertas** habilitadas
- **Frecuencia** de notificaciones
- **Métodos** de notificación
- **Destinatarios** de alertas

#### 4. **Configuración de Seguridad**
- **Políticas de contraseñas**
- **Tiempo de sesión**
- **Acceso por IP**
- **Auditoría de accesos**

#### 5. **Configuración de Integración**
- **Servidores de correo**
- **APIs externas**
- **Sincronización** con sistemas
- **Backup automático**

### Configuración de Alertas

#### Tipos de Alertas Configurables
- **Vencimientos** de registros
- **Documentos faltantes**
- **Cambios de estado** en trámites
- **Eventos del calendario**
- **Errores del sistema**

#### Configuración de Tiempos
```
┌─────────────────────────────────────────────────────────────────┐
│                    Configuración de Alertas                     │
├─────────────────────────────────────────────────────────────────┤
│ Vencimiento de Registros:                                       │
│   ⚠️ Alerta temprana: [30] días antes                          │
│   🚨 Alerta crítica: [7] días antes                            │
│                                                                 │
│ Documentos Faltantes:                                           │
│   ⚠️ Alerta temprana: [15] días antes                          │
│   🚨 Alerta crítica: [3] días antes                            │
│                                                                 │
│ [💾 Guardar Configuración]                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Configuración de Usuarios

#### Roles y Permisos
- **Asignación de roles** por usuario
- **Permisos específicos** por funcionalidad
- **Restricciones** de acceso
- **Configuración** de grupos

#### Configuración de Sesiones
- **Tiempo de inactividad** antes de cerrar sesión
- **Máximo de sesiones** simultáneas
- **Configuración** de recordatorio de contraseña
- **Políticas** de seguridad

---

## 👤 Perfil de Usuario

### ¿Qué es el Perfil de Usuario?

Su perfil de usuario contiene toda la información personal y profesional asociada a su cuenta en el sistema SEGAR.

### Acceso al Perfil

1. **Haga clic en su nombre** en la barra superior
2. **Seleccione "Perfil"** del menú desplegable
3. **O haga clic en "Perfil"** en el menú lateral

### Información del Perfil

#### 1. **Información Personal**
```
┌─────────────────────────────────────────────────────────────────┐
│                      👤 Perfil de Usuario                       │
├─────────────────────────────────────────────────────────────────┤
│ Foto: [👤] [Cambiar Foto]                                       │
│                                                                 │
│ Nombre: [Juan] [Pérez]                                          │
│ Usuario: [jperez] (no editable)                                │
│ Email: [juan.perez@empresa.com]                                │
│ Rol: [Empleado]                                                │
│                                                                 │
│ Teléfono: [300-123-4567]                                       │
│ Teléfono Alt: [601-234-5678]                                   │
│ Dirección: [Calle 123 #45-67]                                  │
│ Ciudad: [Bogotá]                                               │
└─────────────────────────────────────────────────────────────────┘
```

#### 2. **Información Profesional**
- **ID de empleado**
- **Departamento**
- **Cargo**
- **Fecha de ingreso**
- **Supervisor**

#### 3. **Configuración de Cuenta**
- **Preferencias de idioma**
- **Zona horaria**
- **Configuración de notificaciones**
- **Tema de interfaz**

### Editar Perfil

#### Información Editable
1. **Nombre y apellido**
2. **Email**
3. **Teléfonos**
4. **Dirección**
5. **Información profesional**
6. **Configuración de cuenta**

#### Información No Editable
- **Nombre de usuario** (asignado por el administrador)
- **Rol** (asignado por el administrador)
- **Fecha de registro**
- **ID de usuario**

### Cambiar Contraseña

#### Proceso de Cambio
1. **Haga clic en "Cambiar Contraseña"**
2. **Ingrese su contraseña actual**
3. **Ingrese la nueva contraseña** (mínimo 8 caracteres)
4. **Confirme la nueva contraseña**
5. **Haga clic en "Actualizar Contraseña"**

#### Requisitos de Contraseña
- **Mínimo 8 caracteres**
- **Al menos una mayúscula**
- **Al menos una minúscula**
- **Al menos un número**
- **Al menos un carácter especial**

### Configuración de Notificaciones

#### Tipos de Notificaciones
- **📧 Correo electrónico**: Notificaciones por email
- **🔔 Notificaciones del sistema**: Alertas en la interfaz
- **📱 Notificaciones push**: Alertas en dispositivos móviles

#### Configuración por Tipo
```
┌─────────────────────────────────────────────────────────────────┐
│                Configuración de Notificaciones                  │
├─────────────────────────────────────────────────────────────────┤
│ Vencimientos de Registros:                                      │
│   ☑️ Correo electrónico                                         │
│   ☑️ Notificación del sistema                                   │
│   ☐ Notificación push                                           │
│                                                                 │
│ Cambios de Estado en Trámites:                                  │
│   ☑️ Correo electrónico                                         │
│   ☑️ Notificación del sistema                                   │
│   ☐ Notificación push                                           │
│                                                                 │
│ [💾 Guardar Configuración]                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Estadísticas del Usuario

#### Actividad Reciente
- **Trámites creados** en el último mes
- **Documentos subidos** en la última semana
- **Correos enviados** en el último día
- **Eventos del calendario** próximos

#### Métricas de Uso
- **Tiempo de sesión** promedio
- **Funcionalidades** más utilizadas
- **Último acceso** al sistema
- **Dispositivos** utilizados

---

## 🔧 Solución de Problemas

### Problemas Comunes y Soluciones

#### 1. **No Puedo Iniciar Sesión**

**Síntomas:**
- Error al ingresar credenciales
- Mensaje de "Usuario o contraseña incorrectos"
- Redirección infinita

**Soluciones:**
1. **Verifique sus credenciales**:
   - Nombre de usuario correcto
   - Contraseña sin espacios adicionales
   - Mayúsculas/minúsculas correctas

2. **Limpie la caché del navegador**:
   - Chrome: Ctrl+Shift+Delete
   - Firefox: Ctrl+Shift+Delete
   - Safari: Cmd+Option+E

3. **Use modo incógnito** para probar
4. **Contacte al administrador** si persiste el problema

#### 2. **El Sistema Carga Lentamente**

**Síntomas:**
- Páginas que tardan en cargar
- Timeouts en las operaciones
- Errores de conexión

**Soluciones:**
1. **Verifique su conexión a internet**
2. **Cierre otras pestañas** del navegador
3. **Reinicie el navegador**
4. **Limpie la caché** del navegador
5. **Intente con otro navegador**

#### 3. **No Puedo Subir Documentos**

**Síntomas:**
- Error al seleccionar archivos
- Archivos no se suben
- Mensaje de "Archivo no válido"

**Soluciones:**
1. **Verifique el formato** del archivo:
   - PDF, DOC, DOCX, XLS, XLSX, JPG, PNG
   - Tamaño máximo: 25MB

2. **Renombre el archivo**:
   - Sin caracteres especiales
   - Sin espacios
   - Máximo 100 caracteres

3. **Intente con otro archivo** para descartar problemas específicos

#### 4. **Los Datos No Se Actualizan**

**Síntomas:**
- Información desactualizada
- Cambios no se reflejan
- Datos inconsistentes

**Soluciones:**
1. **Actualice la página** (F5)
2. **Use el botón "Actualizar"** en el dashboard
3. **Limpie la caché** del navegador
4. **Verifique su conexión** a internet
5. **Contacte al administrador** si persiste

#### 5. **Error al Crear Trámites**

**Síntomas:**
- Formulario no se envía
- Error al guardar datos
- Información se pierde

**Soluciones:**
1. **Complete todos los campos obligatorios** (marcados con *)
2. **Verifique el formato** de fechas y números
3. **Guarde como borrador** antes de enviar
4. **Use un navegador actualizado**
5. **Contacte al administrador** si persiste

### Códigos de Error Comunes

#### Errores HTTP
| Código | Descripción | Solución |
|--------|-------------|----------|
| **400** | Solicitud incorrecta | Verifique los datos enviados |
| **401** | No autorizado | Inicie sesión nuevamente |
| **403** | Prohibido | Contacte al administrador |
| **404** | No encontrado | Verifique la URL |
| **500** | Error del servidor | Contacte al administrador |

#### Errores de Validación
| Error | Descripción | Solución |
|-------|-------------|----------|
| **Campo requerido** | Falta información obligatoria | Complete el campo |
| **Formato inválido** | Formato incorrecto | Use el formato correcto |
| **Tamaño excedido** | Archivo muy grande | Reduzca el tamaño |
| **Tipo no válido** | Tipo de archivo no permitido | Use un formato válido |

### Contacto de Soporte

#### Información de Contacto
- **Email**: soporte@segar.com
- **Teléfono**: +57 (1) 234-5678
- **Horario**: Lunes a Viernes, 8:00 AM - 6:00 PM

#### Información a Incluir en el Reporte
1. **Descripción detallada** del problema
2. **Pasos para reproducir** el error
3. **Captura de pantalla** del error
4. **Navegador y versión** utilizada
5. **Sistema operativo**
6. **Hora y fecha** del problema

---

## ❓ Preguntas Frecuentes

### Preguntas Generales

#### **¿Qué navegadores son compatibles?**
- **Chrome** 90 o superior
- **Firefox** 88 o superior
- **Safari** 14 o superior
- **Edge** 90 o superior

#### **¿Puedo usar el sistema desde mi móvil?**
Sí, el sistema es **responsive** y se adapta a dispositivos móviles, aunque se recomienda usar una computadora para funciones complejas.

#### **¿Cuántos usuarios pueden usar el sistema simultáneamente?**
El número depende de la configuración de su servidor. Contacte al administrador para conocer los límites específicos.

#### **¿Los datos están seguros?**
Sí, el sistema utiliza **encriptación SSL** y cumple con estándares de seguridad empresarial.

### Preguntas sobre Trámites

#### **¿Cuánto tiempo toma procesar un trámite?**
Los tiempos varían según el tipo:
- **Registro Sanitario**: 90-120 días hábiles
- **Renovación**: 60-90 días hábiles
- **Modificación**: 45-60 días hábiles

#### **¿Puedo cancelar un trámite?**
Sí, puede cancelar trámites en estado "Borrador". Los trámites enviados al INVIMA no se pueden cancelar desde el sistema.

#### **¿Qué documentos necesito para un registro sanitario?**
Los documentos básicos incluyen:
- Ficha técnica del producto
- Etiquetas del producto
- Certificados de análisis
- Documentos de la empresa

#### **¿Puedo subir documentos después de enviar el trámite?**
Sí, si el INVIMA solicita documentos adicionales, puede subirlos a través del sistema.

### Preguntas sobre Usuarios

#### **¿Cómo cambio mi contraseña?**
1. Vaya a su **Perfil de Usuario**
2. Haga clic en **"Cambiar Contraseña"**
3. Siga las instrucciones en pantalla

#### **¿Puedo tener múltiples roles?**
No, cada usuario tiene un solo rol asignado. Si necesita diferentes permisos, contacte al administrador.

#### **¿Qué pasa si olvido mi nombre de usuario?**
Contacte al administrador del sistema, quien puede proporcionarle su nombre de usuario.

#### **¿Puedo acceder desde diferentes computadoras?**
Sí, puede acceder desde cualquier computadora con conexión a internet y un navegador compatible.

### Preguntas sobre Documentos

#### **¿Qué tipos de archivos puedo subir?**
- **PDF**: Documentos principales
- **DOC/DOCX**: Documentos editables
- **XLS/XLSX**: Hojas de cálculo
- **JPG/PNG**: Imágenes
- **ZIP**: Archivos comprimidos

#### **¿Cuál es el tamaño máximo de archivo?**
El tamaño máximo es **25MB** por archivo.

#### **¿Puedo descargar mis documentos?**
Sí, puede descargar cualquier documento que haya subido o que haya recibido.

#### **¿Los documentos se almacenan de forma segura?**
Sí, todos los documentos se almacenan con encriptación y respaldo automático.

### Preguntas sobre Notificaciones

#### **¿Cómo configuro las notificaciones?**
1. Vaya a **Configuración**
2. Seleccione **"Notificaciones"**
3. Configure sus preferencias

#### **¿Puedo recibir notificaciones por email?**
Sí, puede configurar notificaciones por email en la sección de configuración.

#### **¿Cómo desactivo las notificaciones?**
Puede desactivar las notificaciones desde la configuración de su perfil o desde la configuración general del sistema.

### Preguntas Técnicas

#### **¿Por qué el sistema se cierra automáticamente?**
Por seguridad, el sistema cierra la sesión después de un período de inactividad. Puede configurar este tiempo en la configuración.

#### **¿Qué hago si el sistema no responde?**
1. Espere unos minutos
2. Actualice la página (F5)
3. Intente con otro navegador
4. Contacte al soporte técnico

#### **¿Puedo usar el sistema sin conexión a internet?**
No, el sistema requiere conexión a internet para funcionar correctamente.

---

## 📞 Soporte y Contacto

### Información de Contacto

#### **Soporte Técnico**
- **Email**: soporte@segar.com
- **Teléfono**: +57 (1) 234-5678
- **Horario**: Lunes a Viernes, 8:00 AM - 6:00 PM

#### **Administrador del Sistema**
- **Email**: admin@segar.com
- **Teléfono**: +57 (1) 234-5679
- **Horario**: Lunes a Viernes, 8:00 AM - 5:00 PM

### Recursos Adicionales

#### **Documentación**
- Manual técnico del sistema
- Guías de configuración
- Documentación de APIs

#### **Capacitación**
- Sesiones de capacitación online
- Videos tutoriales
- Webinars regulares

#### **Comunidad**
- Foro de usuarios
- Base de conocimientos
- Actualizaciones del sistema

---

## 📝 Notas Finales

### Versión del Manual
- **Versión**: 1.0
- **Fecha**: Enero 2024
- **Última actualización**: Enero 2024

### Agradecimientos
Gracias por utilizar SEGAR. Este manual fue creado para ayudarle a aprovechar al máximo todas las funcionalidades del sistema.

### Feedback
Si tiene sugerencias para mejorar este manual o el sistema, no dude en contactarnos a través de los canales de soporte.

---

**© 2024 SEGAR - Software Especializado para la Gestión de Asuntos Regulatorios**
