# Plan de Administración de Proyecto de Software (SPMP)
## Sistema de Gestión de Trámites Regulatorios (SEGAR)

---

## Historial de Cambios

| Versión | Fecha | Descripción | Autor |
|---------|-------|-------------|-------|
| 1.0 | Septiembre 2025 | Versión inicial del SPMP basada en implementación actual | Equipo SEGAR |

---

## Prefacio

Este documento presenta el Plan de Administración de Proyecto de Software (SPMP) para el Sistema de Gestión de Trámites Regulatorios (SEGAR), una aplicación web desarrollada para automatizar la gestión de trámites regulatorios en el sector de alimentos procesados en Colombia. El documento está estructurado según las mejores prácticas IEEE 1058 y refleja el estado actual de implementación del proyecto.

---

## Tabla de Contenidos

1. [Vista General del Proyecto](#vista-general-del-proyecto)
2. [Contexto del Proyecto](#contexto-del-proyecto)
3. [Administración del Proyecto](#administración-del-proyecto)
4. [Monitoreo y Control del Proyecto](#monitoreo-y-control-del-proyecto)
5. [Entrega del Producto](#entrega-del-producto)
6. [Procesos de Soporte](#procesos-de-soporte)
7. [Anexos](#anexos)
8. [Referencias](#referencias)

---

## Vista General del Proyecto

### Propósito

Que el lector pueda entender los aspectos más importantes del proyecto sin tener que leerlo en su totalidad. Esta sección, si bien es útil para los participantes del proyecto, es también de suma importancia para el cliente, por lo que debe estar escrito en un lenguaje que entienda el cliente.

Actualmente las empresas que venden productos de consumo humano en Colombia se tienen que enfrentar a demoras recurrentes a la hora de realizar sus trámites regulatorios ante la entidad reguladora, en este caso el INVIMA, para así poder sacar sus productos al mercado. Este proyecto tiene como objetivo principal desarrollar el prototipo funcional de una aplicación que les permita a las pequeñas y medianas empresas del sector de alimentos procesados poder gestionar sus trámites regulatorios de una manera más automatizada y así reducir tanto el tiempo que toma cada trámite en realizarse como los errores en la creación de los mismos.

### Visión del Producto

El producto de software propuesto es una aplicación web diseñada para automatizar la gestión de trámites regulatorios en el sector de alimentos procesados en Colombia. Esta orientada principalmente a las micro, pequeñas y medianas empresas (MIPYMES).

Esta solución tecnológica ofrecerá funcionalidades clave como la digitalización del proceso de registros sanitarios, almacenamiento seguro de documentación, alertas automáticas de vencimientos, y seguimiento en tiempo real del estado de los trámites, todo desde una interfaz centralizada. Su diseño contempla la parametrización normativa, permitiendo adaptarse fácilmente a futuros cambios regulatorios sin necesidad de modificar su arquitectura base.

La visión del producto es convertirse en un estándar digital de gestión regulatoria en el sector alimenticio colombiano, nivelando las condiciones entre pequeñas y grandes empresas. Una vez implementado, se espera que el producto contribuya significativamente a la reducción de errores documentales, mejora de la trazabilidad, disminución de tiempos y costos operativos, y en general, a una mayor competitividad y formalización de las MIPYMES del sector. Al combinar un flujo estandarizado con tecnologías ágiles, se posiciona como un catalizador para la competitividad del sector alimentario colombiano, asegurando que innovación y cumplimiento legal vayan de la mano.

### Propósito, Alcance y Objetivos

#### Propósito

El propósito de este proyecto es abordar las serias dificultades que enfrentan las MIPYMES del sector de alimentos procesados en Colombia para gestionar sus trámites regulatorios ante entidades como el INVIMA. La gestión manual actual es compleja, demandante en tiempo y recursos, ineficiente en seguimiento y control, y propensa a errores. Se busca desarrollar una herramienta tecnológica para automatizar o semi-automatizar este proceso, reduciendo la carga administrativa, el tiempo y mejorando el cumplimiento normativo.

El proyecto busca concretar parcialmente la visión del producto, sentando las bases para una solución escalable y adaptable que, en versiones futuras, pueda integrar funcionalidades más avanzadas. A corto plazo, este prototipo servirá como punto de validación funcional en un entorno real mediante su implementación piloto en una MIPYME del sector de alimentos procesados.

Mediante este proyecto se busca reducir los errores comunes en el manejo documental, disminuir tiempos y costos en la gestión regulatoria, y facilitar el cumplimiento normativo en empresas con recursos limitados.

#### Alcance

Se enfoca en un prototipo funcional de una aplicación web en la nube para MIPYMES del sector de alimentos procesados en Colombia, específicamente para trámites de registros sanitarios nacionales. Se busca automatizar las etapas clave identificadas en el proceso regulatorio basado en el caso de estudio previamente realizado con Colombina S.A. Los elementos que se incluyen y excluyen del alcance del proyecto serían los siguientes:

**Incluye:**
- Diseño e implementación de una aplicación web tipo SaaS para la gestión de trámites regulatorios
- Funcionalidades:
  - Digitalización de los trámites clave (registro, renovación, modificación)
  - Almacenamiento seguro y centralizado de documentación de soporte
  - Control de versiones documentales
  - Alertas automáticas sobre vencimiento de registros
  - Visualización en tiempo real del estado de los trámites
  - Dashboard centralizado para monitorear y gestionar actividades clave
  - Adaptabilidad a futuros cambios regulatorios
  - Gestión de Usuarios con diferentes roles
- Pruebas piloto con una MIPYME del sector de alimentos procesados

**No incluye:**
- Integración directa con las plataformas del INVIMA (por limitaciones institucionales)
- Desarrollo móvil nativo (solo interfaz web adaptable)
- Funcionalidades avanzadas como inteligencia artificial, análisis predictivo o minería de procesos
- Adaptación a sectores diferentes al de alimentos procesados (cosméticos, medicamentos, etc)
- Soporte multilingüe o internacionalización

#### Objetivos

**General:**
Desarrollar el prototipo funcional de una aplicación para la gestión de los trámites regulatorios en pequeñas empresas del sector de alimentos procesados.

**Específicos:**
1. Diseñar las bases del sistema considerando los atributos de calidad del software.
2. Desarrollar el prototipo funcional de la aplicación para la gestión de trámites regulatorios en la industria de alimentos procesados.
3. Implementar el prototipo funcional en el caso de prueba con una MIPYME del sector de alimentos procesados.
4. Evaluar el desempeño del producto mediante las pruebas con el caso de estudio de una MIPYME del sector de alimentos procesados.

### Supuestos y Restricciones

#### Supuestos

Durante la planeación y ejecución del proyecto se han considerado ciertos elementos como verdaderos o garantizados para poder llevar a cabo el desarrollo del prototipo de manera adecuada.

Estos supuestos serían los siguientes:

1. **Acceso a información y procesos internos de la empresa piloto**: Se asume que la empresa piloto con la que se trabajará brindará acceso suficiente a su equipo, documentación, procesos actuales y tiempos estimados relacionados con la gestión de trámites regulatorios, para poder validar y evaluar el prototipo funcional.

2. **Estabilidad de los lineamientos regulatorios durante el desarrollo**: Se asume que la normativa del INVIMA relevante para el desarrollo del prototipo no sufrirá cambios sustanciales durante el periodo del proyecto. De lo contrario, podrían requerirse ajustes técnicos o conceptuales importantes.

3. **Disponibilidad del equipo desarrollador**: Se da por sentado que los miembros del equipo de trabajo (estudiantes) contarán con el tiempo y los recursos necesarios para cumplir con las fases metodológicas propuestas.

4. **Acceso a tecnologías necesarias**: Se asume que se podría utilizar sin restricciones herramientas tecnológicas clave, como servicios de autenticación, bases de datos y frameworks web necesarios para el desarrollo del prototipo.

5. **Buena disposición de los usuarios para la prueba piloto**: Se asume que los usuarios clave de la empresa piloto estarán disponibles para participar en sesiones de prueba, retroalimentación y capacitación.

#### Restricciones

Existen condiciones que limitan parcial o totalmente la ejecución del proyecto o el producto resultante. Estas restricciones han sido identificadas de la siguiente manera:

1. **Sin integración directa con INVIMA**: La aplicación no contempla una integración API oficial con las plataformas del INVIMA, debido a restricciones de acceso y falta de disponibilidad pública de dichos servicios.

2. **Limitaciones presupuestales**: El proyecto se desarrollará sin financiación externa, utilizando herramientas de bajo costo o gratuitas, lo que restringe el uso de plataformas comerciales.

3. **Alcance limitado al sector de alimentos procesados**: Aunque la arquitectura es escalable, la solución está diseñada exclusivamente para las MIPYMES del sector de alimentos procesados, sin considerar otras industrias reguladas como cosméticos, farmacéutica o dispositivos médicos.

4. **Restricción temporal**: El proyecto debe completarse dentro del cronograma académico del curso y el periodo asignado por la Universidad, lo que condiciona el número de funcionalidades implementadas.

5. **Infraestructura técnica restringida**: El prototipo se alojará en servidores gratuitos o de bajo costo, lo que podría limitar el rendimiento, almacenamiento o número de usuarios concurrentes durante la fase de prueba.

### Entregables

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Resumen de Calendarización y Presupuesto

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Evolución del Plan

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

---

## Contexto del Proyecto

### Propósito

Explicar los aspectos más importantes del funcionamiento interno del proyecto, así como su comunicación con entidades externas.

### Modelo de Ciclo de Vida

#### Propósito

Que el lector sepa el o los modelos de procesos en que se basa el proyecto, así como los métodos y prácticas que se utilizarán.

El proyecto sigue un modelo de ciclo de vida basado en Investigación en la Ciencia del Diseño (Design Science Research), el cual se complementa con metodologías ágiles ampliamente reconocidas como KanBan y SCRUM. En consecuencia, el proyecto se organiza en cuatro fases metodológicas claramente definidas que permiten gestionar de forma ordenada la evolución del sistema desde su concepción hasta su validación en un entorno real.

**Fase 1 - Diseño de la Solución**: Esta fase se enfoca en establecer las bases del sistema mediante la elaboración de la arquitectura general, el diseño de la interfaz de usuario y la selección de las tecnologías más adecuadas. Durante esta etapa, se implementará el uso de Kanban para visualizar y controlar el progreso de tareas específicas, permitiendo una gestión eficiente y continua del diseño.

**Fase 2 - Desarrollo del Prototipo Funcional**: En esta etapa se procederá a la implementación progresiva del sistema utilizando iteraciones ágiles bajo la metodología SCRUM. Se priorizarán funcionalidades clave que reflejen los procesos centrales de los trámites regulatorios. Cada iteración incluirá fases de planeación, desarrollo, revisión y retroalimentación.

**Fase 3 - Implementación Piloto**: Una vez construido el prototipo, se realizará su implementación y prueba en una MIPYME del sector de alimentos procesados. Se espera recopilar información valiosa en un entorno real, midiendo la utilidad, estabilidad y facilidad de uso de la solución.

**Fase 4 - Evaluación**: Finalmente, se analizarán los resultados obtenidos en la implementación piloto, documentando tanto los aciertos como las oportunidades de mejora. Esta fase también contempla la generación de reportes finales, documentación técnica y recomendaciones para futuras iteraciones o escalamiento del sistema.

#### Prácticas Específicas

- **Kanban**: Herramienta de gestión visual que será utilizada para organizar, priorizar y hacer seguimiento a las actividades durante la etapa de diseño del sistema.
- **SCRUM**: Marco ágil que permitirá estructurar el desarrollo del prototipo en ciclos iterativos de corta duración (sprints), fomentando entregas parciales, evaluaciones frecuentes y ajustes continuos.
- **Validación por sprints**: Cada sprint culminará con una revisión del producto entregado lo que permitirá asegurar la alineación con los requerimientos y detectar desviaciones de manera oportuna.
- **Uso de IEEE/ISO**: Se emplearán normas como IEEE 830, 1016, 1058 y 829, así como la ISO/IEC 25010, para asegurar la calidad del producto, la trazabilidad de los requisitos y la coherencia de la documentación.
- **Revisión entre pares**: Como práctica continua, cada entregable será revisado por al menos otro miembro del equipo para asegurar claridad, coherencia, completitud y calidad técnica antes de ser validado o entregado oficialmente.

#### Análisis de Alternativas y Justificación

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Lenguajes y Herramientas

#### Propósito

Que el lector sepa qué lenguajes de modelado y programación, así como las herramientas que se utilizarán en el proyecto (desde ofimática hasta herramientas para pruebas automáticas y de control y administración de versiones).

#### Herramientas y Tecnologías Implementadas

Basado en el análisis del código fuente actual, se han identificado las siguientes tecnologías y herramientas implementadas:

##### Backend (Spring Boot)

**Framework Principal:**
- **Spring Boot 3.5.2**: Framework principal para el desarrollo del backend
- **Java 21**: Lenguaje de programación principal
- **Spring Modulith 1.1.4**: Para arquitectura modular

**Dependencias Principales:**
- **Spring Boot Starter Web**: Para desarrollo de APIs REST
- **Spring Boot Starter Data JPA**: Para persistencia de datos
- **Spring Boot Starter Security**: Para seguridad de la aplicación
- **Spring Boot Starter OAuth2 Resource Server**: Para autenticación con Keycloak
- **Spring Boot Starter Thymeleaf**: Para generación de documentos PDF
- **Spring Boot Starter Validation**: Para validación de datos
- **Spring Boot Starter Mail**: Para funcionalidad de correo electrónico

**Base de Datos:**
- **H2 Database**: Base de datos en memoria para desarrollo y pruebas
- **PostgreSQL**: Base de datos de producción (configurada pero no activa en desarrollo)
- **JPA/Hibernate**: ORM para manejo de persistencia

**Herramientas de Desarrollo:**
- **Lombok**: Para reducción de código boilerplate
- **Maven**: Herramienta de construcción y gestión de dependencias
- **Spring Boot DevTools**: Para desarrollo ágil

**Generación de Documentos:**
- **OpenHTMLToPDF 1.0.10**: Para generación de documentos PDF
- **Thymeleaf**: Motor de plantillas para documentos

**Testing:**
- **Spring Boot Starter Test**: Framework de testing
- **Spring Security Test**: Testing de seguridad
- **TestContainers**: Para testing de integración con contenedores
- **RestAssured**: Para testing de APIs REST
- **WireMock**: Para mock de servicios externos
- **AssertJ**: Para assertions más legibles

**Documentación:**
- **SpringDoc OpenAPI 2.2.0**: Para documentación automática de APIs (Swagger)

##### Frontend (Angular)

**Framework Principal:**
- **Angular 19.2.0**: Framework principal para el desarrollo del frontend
- **TypeScript 5.7.2**: Lenguaje de programación principal

**Dependencias Principales:**
- **Angular Router**: Para navegación entre páginas
- **Angular Forms**: Para manejo de formularios
- **RxJS 7.8.0**: Para programación reactiva

**Autenticación:**
- **Keycloak JS 23.0.0**: Cliente JavaScript para integración con Keycloak

**UI y Estilos:**
- **Tailwind CSS 3.3.5**: Framework de CSS utilitario
- **Chart.js 4.5.0**: Para gráficos y visualizaciones

**Herramientas de Desarrollo:**
- **Angular CLI 19.2.17**: Herramienta de línea de comandos
- **Angular DevKit**: Para herramientas de construcción
- **Karma**: Para testing unitario
- **Jasmine**: Framework de testing

**Build Tools:**
- **Webpack**: Bundler de módulos (incluido en Angular CLI)
- **PostCSS**: Para procesamiento de CSS
- **Autoprefixer**: Para compatibilidad de CSS

##### Autenticación y Seguridad

**Keycloak:**
- **Versión**: 23.0.0
- **Puerto**: 8080
- **Realm**: segar
- **Cliente**: segar-frontend (público)
- **Flujo**: Resource Owner Password Flow

**Usuarios Configurados:**
- **Administrador**: admin.segar / admin123 (rol: admin)
- **Empleado**: empleado.segar / empleado123 (rol: empleado)

##### Infraestructura y Despliegue

**Servidores de Desarrollo:**
- **Backend**: localhost:8090
- **Frontend**: localhost:4200
- **Keycloak**: localhost:8080
- **H2 Console**: localhost:8090/h2

**Configuración SMTP:**
- **Host**: smtp.gmail.com
- **Puerto**: 587
- **Protocolo**: IMAP para lectura de correos

##### Herramientas de Gestión de Proyecto

**Control de Versiones:**
- **Git**: Sistema de control de versiones
- **GitHub**: Repositorio remoto

**Documentación:**
- **Markdown**: Para documentación del proyecto
- **Swagger/OpenAPI**: Para documentación de APIs

### Plan de Aceptación del Producto

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Organización del Proyecto y Comunicación

#### Organigrama y Descripción de Roles

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

---

## Administración del Proyecto

**EN PROCESO**: Toda esta sección se definirá una vez completado la fase 1 del proyecto.

### Métodos y Herramientas de Estimación

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Inicio del Proyecto

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

#### Entrenamiento del Personal

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

#### Infraestructura

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Planes de Trabajo del Proyecto

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

#### Descomposición de Actividades

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

#### Calendarización

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

**SE DEBE ACTUALIZAR EN TODAS LAS ENTREGAS**

#### Asignación de Recursos

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

#### Asignación de Presupuesto y Justificación

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

---

## Monitoreo y Control del Proyecto

**EN PROCESO**: Toda esta sección se definirá una vez completado la fase 1 del proyecto.

### Administración de Requerimientos

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Monitoreo y Control de Progreso

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

### Cierre del Proyecto

**EN PROCESO**: Se definirá una vez completada la fase 1 del proyecto.

---

## Entrega del Producto

**EN PROCESO**: Toda esta sección se definirá una vez completado la fase 2 del proyecto.

---

## Procesos de Soporte

**EN PROCESO**: Toda esta sección se definirá una vez completado la fase 2 del proyecto.

### Ambiente de Trabajo

**EN PROCESO**: Se definirá una vez completada la fase 2 del proyecto.

### Análisis y Administración de Riesgos

**EN PROCESO**: Se definirá una vez completada la fase 2 del proyecto.

### Administración de Configuración y Documentación

**EN PROCESO**: Se definirá una vez completada la fase 2 del proyecto.

### Métricas y Proceso de Medición

**EN PROCESO**: Se definirá una vez completada la fase 2 del proyecto.

### Control de Calidad

**EN PROCESO**: Se definirá una vez completada la fase 2 del proyecto.

---

## Anexos

### Anexo A: Módulos Implementados

#### Backend - Módulos Principales

**1. Módulo de Trámites (`tramites`)**
- **Funcionalidad**: Gestión completa del ciclo de vida de trámites regulatorios
- **Controladores**: 
  - `TramitesController`: Tracking, timeline, requerimientos, notificaciones
  - `TramiteResolucionController`: Gestión de resoluciones
  - `SolicitudController`: Manejo de solicitudes
  - `RadicacionController`: Proceso de radicación
  - `PagosController`: Gestión de pagos
  - `ValidacionesController`: Validaciones de documentos
- **Entidades**: Tramite, Solicitud, Pago, Resolucion, RegistroSanitario
- **Servicios**: TramiteServiceImpl con funcionalidades completas

**2. Módulo de Documentos (`documentos`)**
- **Funcionalidad**: Gestión de documentos dinámicos y plantillas
- **Controladores**:
  - `DocumentosController`: CRUD de documentos
  - `DocumentTemplateController`: Gestión de plantillas
  - `DocumentInstanceController`: Instancias de documentos
  - `DocumentosDisponiblesController`: Catálogo de documentos
  - `FileDownloadController`: Descarga de archivos
- **Entidades**: Documento, DocumentTemplate, DocumentInstance
- **Servicios**: Generación de PDF, almacenamiento local, validación

**3. Módulo de Calendario (`calendario`)**
- **Funcionalidad**: Gestión de eventos y recordatorios
- **Controladores**: `CalendarioController`
- **Entidades**: Evento, CategoriaEvento, EstadoEvento, PrioridadEvento, TipoEvento
- **Servicios**: CalendarioService, EventoService

**4. Módulo de Notificaciones (`notificaciones`)**
- **Funcionalidad**: Sistema de correo electrónico
- **Controladores**: `EmailController`
- **Entidades**: Email, EmailAttachment, EmailContent
- **Servicios**: EmailService con soporte SMTP e IMAP

**5. Módulo de Gestión de Usuarios (`gestionUsuarios`)**
- **Funcionalidad**: Integración con Keycloak para gestión de usuarios
- **Integración**: Keycloak para autenticación y autorización

**6. Módulo de Seguridad (`security`)**
- **Funcionalidad**: Configuración de seguridad OAuth2
- **Controladores**: `AuthController`
- **Configuración**: SecurityConfig con JWT validation

**7. Módulo Compartido (`shared`)**
- **Funcionalidad**: Entidades y servicios compartidos
- **Entidades**: Producto, Empresa, enums de estados
- **Servicios**: ProductoServiceImpl

**8. Módulo Gateway (`gateway`)**
- **Funcionalidad**: API unificada y proxy
- **Controladores**: `UnifiedApiController`, `APITramitesController`

#### Frontend - Componentes Principales

**1. Módulo de Autenticación (`auth`)**
- **Componentes**: LoginFormComponent, AuthPageComponent, RecoverFormComponent
- **Servicios**: AuthService con integración Keycloak
- **Guards**: AuthGuard para protección de rutas
- **Interceptors**: AuthInterceptor para JWT automático

**2. Módulo de Layout (`layout`)**
- **Componentes**: MenuLayoutComponent, MenuLateralComponent, BarraSuperiorComponent
- **Funcionalidad**: Navegación principal y estructura de la aplicación

**3. Módulo de Páginas (`pages`)**
- **Dashboard**: DashboardComponent
- **Panel Principal**: PanelPrincipalComponent
- **Calendario**: CalendarioComponent
- **Nuevo Trámite**: NuevoTramiteComponent
- **Gestión de Usuarios**: UsuariosComponent, UserProfileComponent
- **Configuración**: ConfiguracionComponent
- **Búsqueda Global**: BusquedaGlobalComponent
- **Correos**: CorreosComponent

**4. Módulo de Trámites (`tramites`)**
- **Registro**: Componentes paso a paso (RegistroPasoUnoComponent, etc.)
- **Consulta**: ConsultaSolicitudesComponent
- **Resolución**: ResolucionCumplimientoComponent

**5. Módulo de Componentes Compartidos (`shared`)**
- **Documentos**: DocumentFormComponent, DocumentListComponent, etc.
- **Generador de Documentos**: GeneradorDocumentoComponent
- **Modales**: TramiteDetalleModalComponent

**6. Módulo de Notificaciones (`components`)**
- **Centro de Notificaciones**: NotificationCenterComponent
- **Configuración**: NotificationSettingsComponent
- **Toast**: NotificationToastComponent

### Anexo B: Base de Datos - Esquema Implementado

```sql
-- Tablas Principales Implementadas
- producto: Gestión de productos
- pago: Gestión de pagos
- solicitud: Solicitudes de trámites
- documento: Documentos asociados
- tramite: Trámites principales
- resolucion: Resoluciones INVIMA
- registro_sanitario: Registros sanitarios
- historial_tramite: Historial de cambios
- evento: Eventos del calendario
- email: Gestión de correos
```

### Anexo C: APIs Implementadas

**Endpoints Principales:**
- `/api/tramites/{id}/tracking` - Seguimiento de trámites
- `/api/tramites/{id}/timeline` - Línea de tiempo
- `/api/tramites/{id}/requerimientos` - Gestión de requerimientos
- `/api/tramites/{id}/notificaciones` - Sistema de notificaciones
- `/api/documentos` - Gestión de documentos
- `/api/calendario` - Gestión de eventos
- `/api/auth` - Autenticación
- `/api/productos` - Gestión de productos

---

## Referencias

1. IEEE Std 1058-1998, IEEE Standard for Software Project Management Plans
2. ISO/IEC 25010:2011, Systems and software Quality Requirements and Evaluation (SQuaRE)
3. Spring Boot Documentation - https://spring.io/projects/spring-boot
4. Angular Documentation - https://angular.io/docs
5. Keycloak Documentation - https://www.keycloak.org/documentation
6. Tailwind CSS Documentation - https://tailwindcss.com/docs

---

**Documento generado automáticamente basado en el análisis del código fuente actual del proyecto SEGAR**
**Fecha de generación**: Septiembre 2025
**Estado**: En desarrollo - Fase 1 completada parcialmente
