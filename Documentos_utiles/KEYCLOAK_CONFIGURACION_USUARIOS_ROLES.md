# 🔐 Configuración de Registro de Usuarios y Permisos con Keycloak en SEGAR

**Guía Completa para Gestión de Usuarios y Roles**  
**Última actualización**: Noviembre 11, 2025

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Configuración Inicial de Keycloak](#configuración-inicial-de-keycloak)
3. [Registro de Nuevos Usuarios](#registro-de-nuevos-usuarios)
4. [Configuración de Roles y Permisos](#configuración-de-roles-y-permisos)
5. [Gestión de Usuarios desde el Sistema](#gestión-de-usuarios-desde-el-sistema)
6. [APIs de Gestión de Usuarios](#apis-de-gestión-de-usuarios)
7. [Configuración de Permisos por Módulo](#configuración-de-permisos-por-módulo)
8. [Troubleshooting](#troubleshooting)

---

## Introducción

SEGAR utiliza Keycloak como sistema de autenticación y autorización. Esto permite:

- ✅ Gestión centralizada de usuarios
- ✅ Autenticación segura con OAuth2/JWT
- ✅ Roles y permisos granulares
- ✅ Single Sign-On (SSO) preparado para futuro
- ✅ Sincronización bidireccional con la base de datos del sistema

---

## Configuración Inicial de Keycloak

### 1. Iniciar Keycloak

```bash
# Navegar al directorio de Keycloak
cd keycloak-23.0.0/bin

# Iniciar en modo desarrollo
./kc.sh start-dev  # Linux/Mac
kc.bat start-dev   # Windows
```

### 2. Acceder a la Consola de Administración

1. Abrir navegador en: `http://localhost:8080`
2. Ir a la consola de administración: `http://localhost:8080/admin`
3. Login con credenciales de administrador:
   - **Usuario**: `admin`
   - **Contraseña**: `admin123` (o la que configuraste al instalar)

### 3. Verificar el Realm "segar"

1. En la esquina superior izquierda, seleccionar el realm **"segar"**
2. Si no existe, crearlo:
   - Click en el dropdown de realms
   - "Create realm"
   - Nombre: `segar`
   - Click en "Create"

### 4. Configurar el Cliente "segar-frontend"

1. Ir a **Clients** en el menú lateral
2. Buscar o crear el cliente `segar-frontend`:
   - **Client ID**: `segar-frontend`
   - **Client Protocol**: `openid-connect`
   - **Access Type**: `public`
   - **Valid Redirect URIs**: `http://localhost:4200/*`
   - **Web Origins**: `http://localhost:4200`
   - **Direct Access Grants Enabled**: `ON` ✅ (Importante para Resource Owner Password Flow)

---

## Registro de Nuevos Usuarios

Hay **dos formas** de registrar nuevos usuarios en SEGAR:

### Opción 1: Desde la Consola de Keycloak (Método Manual)

#### Paso 1: Crear el Usuario

1. En Keycloak Admin Console, ir a **Users**
2. Click en **Add user**
3. Completar el formulario:
   - **Username**: `empleado.nuevo` (obligatorio, único)
   - **Email**: `empleado.nuevo@segar.gov.co`
   - **First Name**: `Empleado`
   - **Last Name**: `Nuevo`
   - **Email Verified**: `ON` ✅
   - **Enabled**: `ON` ✅
4. Click en **Create**

#### Paso 2: Configurar Contraseña

1. Ir a la pestaña **Credentials**
2. Click en **Set password**
3. Ingresar:
   - **Password**: `contraseña_segura`
   - **Password Confirmation**: `contraseña_segura`
   - **Temporary**: `OFF` ❌ (si quieres que sea permanente)
4. Click en **Set password**

#### Paso 3: Asignar Roles

1. Ir a la pestaña **Role Mappings**
2. En **Client Roles**, seleccionar `segar-backend`
3. En **Available Roles**, seleccionar el rol deseado (ej: `empleado`, `admin`)
4. Click en **Add selected** →

### Opción 2: Desde el Sistema SEGAR (Método Automatizado) ✨

Este es el método recomendado porque sincroniza automáticamente con Keycloak.

#### Usando la API REST

```http
POST http://localhost:8090/api/usuarios
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "username": "empleado.nuevo",
  "email": "empleado.nuevo@segar.gov.co",
  "firstName": "Empleado",
  "lastName": "Nuevo",
  "password": "contraseña_segura",
  "telefono": "3001234567",
  "cargo": "Analista de Trámites",
  "departamento": "Operaciones",
  "rol": "empleado",
  "activo": true
}
```

**Respuesta Exitosa**:
```json
{
  "id": 5,
  "username": "empleado.nuevo",
  "email": "empleado.nuevo@segar.gov.co",
  "firstName": "Empleado",
  "lastName": "Nuevo",
  "telefono": "3001234567",
  "cargo": "Analista de Trámites",
  "departamento": "Operaciones",
  "rol": "empleado",
  "activo": true,
  "keycloakId": "abc123-def456-ghi789",
  "fechaCreacion": "2025-11-11T10:30:00Z"
}
```

#### Usando el Frontend

1. Login como **admin**
2. Ir a **Gestión de Usuarios** en el menú
3. Click en **Nuevo Usuario** o **+**
4. Completar el formulario:
   - Información básica (username, email, nombres)
   - Contraseña
   - Información laboral (cargo, departamento)
   - Rol (admin, empleado)
5. Click en **Guardar**

El sistema automáticamente:
- ✅ Crea el usuario en Keycloak
- ✅ Asigna el rol correspondiente
- ✅ Guarda la información completa en la base de datos local
- ✅ Sincroniza ambos sistemas

---

## Configuración de Roles y Permisos

### Roles Predefinidos en SEGAR

SEGAR tiene los siguientes roles configurados:

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **admin** | Administrador del sistema | Acceso completo a todas las funcionalidades |
| **empleado** | Empleado de SEGAR | Gestión de trámites, documentos y comunicación |
| **mipyme** | Empresa del sector alimentario | Consulta de sus propios trámites |

### Crear Nuevos Roles en Keycloak

#### Paso 1: Crear el Rol en el Cliente

1. En Keycloak Admin, ir a **Clients** → `segar-backend`
2. Click en la pestaña **Roles**
3. Click en **Add Role**
4. Completar:
   - **Role Name**: `supervisor` (por ejemplo)
   - **Description**: `Supervisor de operaciones con permisos limitados`
5. Click en **Save**

#### Paso 2: Configurar el Rol en el Backend

Agregar el nuevo rol en `SecurityConfig.java`:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints solo para ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Endpoints para ADMIN y EMPLEADO
                .requestMatchers(HttpMethod.POST, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers(HttpMethod.PUT, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/api/documentos/**").hasAnyRole("ADMIN", "EMPLEADO")
                
                // Endpoints para ADMIN, EMPLEADO y SUPERVISOR (nuevo)
                .requestMatchers(HttpMethod.GET, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO", "SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasAnyRole("ADMIN", "EMPLEADO", "SUPERVISOR")
                
                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .build();
    }
}
```

### Asignar Roles a Usuarios Existentes

#### Desde Keycloak

1. Ir a **Users**
2. Seleccionar el usuario
3. Pestaña **Role Mappings**
4. En **Client Roles**, seleccionar `segar-backend`
5. Seleccionar el rol y click en **Add selected** →

#### Desde la API de SEGAR

```http
PUT http://localhost:8090/api/usuarios/5
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "rol": "supervisor"
}
```

---

## Gestión de Usuarios desde el Sistema

### APIs Disponibles

#### 1. Obtener Todos los Usuarios (Local)

```http
GET http://localhost:8090/api/usuarios/local
Authorization: Bearer <admin_token>
```

#### 2. Sincronizar con Keycloak

```http
GET http://localhost:8090/api/usuarios
Authorization: Bearer <admin_token>
```

#### 3. Crear Usuario

```http
POST http://localhost:8090/api/usuarios
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "username": "nuevo.usuario",
  "email": "nuevo@segar.gov.co",
  "firstName": "Nuevo",
  "lastName": "Usuario",
  "password": "contraseña123",
  "telefono": "3001234567",
  "cargo": "Analista",
  "departamento": "Operaciones",
  "rol": "empleado",
  "activo": true
}
```

#### 4. Actualizar Usuario

```http
PUT http://localhost:8090/api/usuarios/5
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "email": "nuevo.email@segar.gov.co",
  "telefono": "3009876543",
  "cargo": "Analista Senior",
  "rol": "supervisor"
}
```

#### 5. Eliminar Usuario

```http
DELETE http://localhost:8090/api/usuarios/5
Authorization: Bearer <admin_token>
```

#### 6. Activar/Desactivar Usuario

```http
PATCH http://localhost:8090/api/usuarios/5/toggle-active
Authorization: Bearer <admin_token>
```

#### 7. Cambiar Contraseña

```http
PATCH http://localhost:8090/api/usuarios/5/password
Content-Type: application/json
Authorization: Bearer <admin_token>

{
  "newPassword": "nueva_contraseña_segura",
  "temporary": false
}
```

#### 8. Buscar por Username

```http
GET http://localhost:8090/api/usuarios/username/empleado.nuevo
Authorization: Bearer <admin_token>
```

---

## Configuración de Permisos por Módulo

### Usando Anotaciones de Seguridad

Puedes controlar el acceso a nivel de método usando anotaciones:

```java
@RestController
@RequestMapping("/api/tramites")
public class TramitesController {

    // Solo ADMIN puede crear trámites
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TramiteDTO> crearTramite(@RequestBody TramiteCreateDTO dto) {
        // ...
    }
    
    // ADMIN y EMPLEADO pueden actualizar
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<TramiteDTO> actualizarTramite(@PathVariable Long id, @RequestBody TramiteUpdateDTO dto) {
        // ...
    }
    
    // ADMIN, EMPLEADO y SUPERVISOR pueden consultar
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'SUPERVISOR')")
    @GetMapping("/{id}")
    public ResponseEntity<TramiteDTO> obtenerTramite(@PathVariable Long id) {
        // ...
    }
    
    // Solo el propietario o ADMIN pueden acceder
    @PreAuthorize("hasRole('ADMIN') or @tramiteService.esPropiedad(#id, authentication.name)")
    @GetMapping("/{id}/detalle")
    public ResponseEntity<TramiteDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        // ...
    }
}
```

### Configuración Granular por Endpoint

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(authz -> authz
            // Gestión de Usuarios - Solo ADMIN
            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
            
            // Trámites - Escritura ADMIN y EMPLEADO
            .requestMatchers(HttpMethod.POST, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO")
            .requestMatchers(HttpMethod.PUT, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO")
            .requestMatchers(HttpMethod.DELETE, "/api/tramites/**").hasRole("ADMIN")
            
            // Trámites - Lectura ADMIN, EMPLEADO y SUPERVISOR
            .requestMatchers(HttpMethod.GET, "/api/tramites/**").hasAnyRole("ADMIN", "EMPLEADO", "SUPERVISOR")
            
            // Documentos - ADMIN y EMPLEADO
            .requestMatchers("/api/documentos/**").hasAnyRole("ADMIN", "EMPLEADO")
            
            // Dashboard - Todos los roles autenticados
            .requestMatchers(HttpMethod.GET, "/api/dashboard/**").authenticated()
            
            // Correos - ADMIN y EMPLEADO
            .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "EMPLEADO")
            
            // Calendario - Todos los roles autenticados
            .requestMatchers("/api/calendario/**").authenticated()
            
            // Cualquier otro endpoint
            .anyRequest().authenticated()
        )
        .build();
}
```

### Protección en el Frontend

Usa guards y directivas para controlar el acceso en Angular:

```typescript
// auth.guard.ts
@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  
  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/login']);
      return false;
    }
    
    // Verificar rol si se especifica en la ruta
    const requiredRole = route.data['role'];
    if (requiredRole && !this.authService.hasRole(requiredRole)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    
    return true;
  }
}

// app.routes.ts
export const routes: Routes = [
  {
    path: 'main',
    component: MenuLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'usuarios',
        component: UsuariosComponent,
        canActivate: [AuthGuard],
        data: { role: 'admin' }  // Solo ADMIN
      },
      {
        path: 'tramites',
        component: TramitesComponent,
        canActivate: [AuthGuard]  // Todos autenticados
      }
    ]
  }
];
```

### Ocultar Elementos según Rol

```typescript
// En el componente
export class MenuLateralComponent {
  constructor(public authService: AuthService) {}
  
  isAdmin(): boolean {
    return this.authService.hasRole('admin');
  }
  
  isEmpleadoOrAdmin(): boolean {
    return this.authService.hasAnyRole(['admin', 'empleado']);
  }
}
```

```html
<!-- En la plantilla HTML -->
<nav class="menu-lateral">
  <!-- Visible para todos los autenticados -->
  <a routerLink="/main/dashboard">Dashboard</a>
  
  <!-- Solo visible para ADMIN y EMPLEADO -->
  <a *ngIf="isEmpleadoOrAdmin()" routerLink="/main/tramites">Trámites</a>
  
  <!-- Solo visible para ADMIN -->
  <a *ngIf="isAdmin()" routerLink="/main/usuarios">Gestión de Usuarios</a>
</nav>
```

---

## Troubleshooting

### Problema: Usuario no puede autenticarse

**Síntoma**: Error 401 Unauthorized al intentar login

**Soluciones**:

1. **Verificar credenciales en Keycloak**:
   - El usuario existe en Keycloak
   - El usuario está habilitado (Enabled = ON)
   - La contraseña es correcta

2. **Verificar cliente**:
   - Client ID correcto: `segar-frontend`
   - Direct Access Grants Enabled = ON
   - Valid Redirect URIs configurado

3. **Verificar conectividad**:
   ```bash
   curl http://localhost:8080/realms/segar/.well-known/openid-configuration
   ```

### Problema: Usuario autenticado pero sin permisos

**Síntoma**: Error 403 Forbidden al acceder a recursos

**Soluciones**:

1. **Verificar roles en Keycloak**:
   - Usuario tiene rol asignado en `segar-backend`
   - Rol está escrito correctamente (minúsculas)

2. **Verificar token JWT**:
   - Decodificar en https://jwt.io
   - Verificar que `resource_access.segar-backend.roles` contiene el rol

3. **Verificar configuración en SecurityConfig**:
   - El endpoint tiene la configuración correcta
   - El rol está incluido en `hasRole()` o `hasAnyRole()`

### Problema: Usuarios no se sincronizan

**Síntoma**: Usuario creado en Keycloak no aparece en SEGAR

**Soluciones**:

1. **Sincronización manual**:
   ```http
   GET http://localhost:8090/api/usuarios
   ```

2. **Verificar configuración de Keycloak Admin Client**:
   ```properties
   keycloak.admin.server-url=http://localhost:8080
   keycloak.admin.realm=segar
   keycloak.admin.client-id=admin-cli
   keycloak.admin.username=admin
   keycloak.admin.password=admin123
   ```

3. **Verificar logs del backend** para errores de conexión

### Problema: Contraseña de aplicación Gmail

**Síntoma**: Error de autenticación al enviar correos

**Solución**:

1. Generar contraseña de aplicación de Gmail:
   - Ir a https://myaccount.google.com/apppasswords
   - Crear nueva contraseña de aplicación
   - Copiar la contraseña generada

2. Actualizar `application.properties`:
   ```properties
   spring.mail.username=tu-correo@gmail.com
   spring.mail.password=xxxx xxxx xxxx xxxx  # Contraseña de aplicación
   ```

---

## Resumen de Configuración Completa

### Checklist para Nuevo Usuario

- [ ] Crear usuario en Keycloak (manual) o SEGAR (automático)
- [ ] Asignar rol apropiado (admin, empleado, supervisor)
- [ ] Configurar contraseña (temporal o permanente)
- [ ] Verificar que el usuario está habilitado
- [ ] Sincronizar con base de datos local (si se creó en Keycloak)
- [ ] Probar login desde el frontend
- [ ] Verificar permisos de acceso a módulos

### Checklist para Nuevo Rol

- [ ] Crear rol en Keycloak (cliente `segar-backend`)
- [ ] Actualizar `SecurityConfig.java` con permisos del rol
- [ ] Actualizar guards en frontend si es necesario
- [ ] Actualizar menús y componentes para mostrar/ocultar según rol
- [ ] Probar acceso a diferentes endpoints con el nuevo rol
- [ ] Documentar permisos del rol

---

## Recursos Adicionales

- **Keycloak Admin Console**: http://localhost:8080/admin
- **Documentación Keycloak**: https://www.keycloak.org/documentation
- **Swagger API**: http://localhost:8090/swagger-ui.html
- **Repositorio Frontend**: https://github.com/Juando26030/SEGAR-FrontEnd
- **Repositorio Backend**: https://github.com/Juando26030/SEGAR-BackEnd

---

**Guía creada para facilitar la configuración de usuarios y permisos en SEGAR**  
**Noviembre 11, 2025**
