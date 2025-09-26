# 🔐 EXPLICACIÓN COMPLETA: AUTENTICACIÓN KEYCLOAK EN SEGAR

## 📊 ARQUITECTURA DEL SISTEMA

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   FRONTEND      │    │    KEYCLOAK     │    │    BACKEND      │
│   Angular       │    │   Auth Server   │    │  Spring Boot    │
│  (Port 4200)    │    │   (Port 8080)   │    │  (Port 8090)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔄 FLUJO DE AUTENTICACIÓN

### Paso 1: Inicio de la Aplicación Angular
```typescript
// app.config.ts - Se ejecuta automáticamente al iniciar
{
  provide: APP_INITIALIZER,
  useFactory: initializeKeycloak,  // ← Inicia Keycloak antes que Angular
  deps: [AuthService],
  multi: true
}
```

### Paso 2: Inicialización de Keycloak
```typescript
// auth.service.ts
async initKeycloak(): Promise<boolean> {
  this.keycloak = new Keycloak({
    url: 'http://localhost:8080',     // ← Servidor Keycloak
    realm: 'segar',                  // ← Realm configurado
    clientId: 'segar-frontend'       // ← Cliente público
  });

  const authenticated = await this.keycloak.init({
    onLoad: 'login-required',        // ← Fuerza login inmediato
    checkLoginIframe: false,
    pkceMethod: 'S256'              // ← Seguridad PKCE
  });
}
```

### Paso 3: Redirección Automática
- ✅ Usuario abre `http://localhost:4200`
- 🔄 Angular detecta que no hay token válido
- 🚀 **Redirección automática** a `http://localhost:8080/realms/segar/protocol/openid-connect/auth`

### Paso 4: Login en Keycloak
```
┌─────────────────────────────────────┐
│        KEYCLOAK LOGIN PAGE          │
│                                     │
│  Usuario: admin.segar              │
│  Password: admin123                │
│                                     │
│  [ Login ]                         │
└─────────────────────────────────────┘
```

### Paso 5: Generación del JWT Token
Después del login, Keycloak genera un JWT que contiene:

```json
{
  "sub": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "preferred_username": "admin.segar",
  "email": "admin@segar.com",
  "resource_access": {
    "segar-backend": {
      "roles": ["ADMIN", "EMPLEADO"]      // ← Roles del usuario
    }
  },
  "exp": 1727306395,                      // ← Expiración del token
  "iat": 1727306095                       // ← Fecha de emisión
}
```

### Paso 6: Regreso a Angular
- 🔙 Keycloak redirige de vuelta a `http://localhost:4200`
- 📥 Angular recibe el token JWT en la URL
- 💾 AuthService guarda el token y carga el perfil del usuario

## 🛡️ PROTECCIÓN DE ENDPOINTS

### En el Frontend (Angular)
```typescript
// auth.interceptor.ts - Intercepta TODAS las requests HTTP
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = authService.getToken();
  
  if (token && req.url.includes('localhost:8090')) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
      //                                         ↑
      //                              Token JWT automático
    });
    return next(authReq);
  }
  return next(req);
};
```

### En el Backend (Spring Boot)
```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")     // ← Solo admins
                .requestMatchers("/api/auth/**").authenticated()       // ← Usuario logueado
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())                            // ← Valida JWT con Keycloak
                    .jwtAuthenticationConverter(jwtConverter())       // ← Extrae roles
                )
            ).build();
    }
}
```

## 🔍 VALIDACIÓN DE TOKENS

### Proceso de Validación
1. **Cliente envía request** con `Authorization: Bearer <jwt-token>`
2. **Spring Security intercepta** la request
3. **JWT Decoder valida**:
   - ✅ Firma del token (con clave pública de Keycloak)
   - ✅ Expiración del token
   - ✅ Emisor del token (Keycloak)
4. **JWT Converter extrae roles** del claim `resource_access.segar-backend.roles`
5. **@PreAuthorize verifica** si el usuario tiene el rol requerido

### Ejemplo de Endpoint Protegido
```java
// AuthController.java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @GetMapping("/user-info")
    public ResponseEntity<UserInfoDTO> getUserInfo(Authentication auth) {
        // ↑ Solo usuarios autenticados pueden acceder
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) auth;
        // Extrae info del JWT automáticamente
    }
    
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")  // ← Solo usuarios con rol ADMIN
    public ResponseEntity<List<String>> getUsers() {
        return ResponseEntity.ok(Arrays.asList("admin.segar", "empleado.segar"));
    }
}
```

## 🚦 GUARDS Y PROTECCIÓN DE RUTAS

### AuthGuard - Protege rutas que requieren login
```typescript
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;                          // ← Permite acceso
    } else {
      this.router.navigate(['/login']);     // ← Redirige al login
      return false;
    }
  }
}
```

### AdminGuard - Protege rutas solo para administradores
```typescript
@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  canActivate(): boolean {
    return this.authService.isAuthenticated() && 
           this.authService.isAdmin();       // ← Verifica rol ADMIN
  }
}
```

## 🔄 RENOVACIÓN AUTOMÁTICA DE TOKENS

```typescript
// auth.service.ts
constructor() {
  // Configura renovación automática
  this.keycloak.onTokenExpired = () => {
    this.refreshToken();                    // ← Renueva token automáticamente
  };
}

async refreshToken(): Promise<boolean> {
  try {
    const refreshed = await this.keycloak.updateToken(30); // 30 segundos antes de expirar
    if (refreshed) {
      console.log('Token refreshed');       // ← Token renovado exitosamente
    }
    return refreshed;
  } catch (error) {
    this.logout();                          // ← Si falla, hacer logout
    return false;
  }
}
```

## 🎯 ROLES Y AUTORIZACIÓN

### Configuración en Keycloak
```
Realm: segar
├── Client: segar-backend (Bearer-only)
├── Client: segar-frontend (Public)
├── Roles:
│   ├── ADMIN    ← Acceso completo
│   └── EMPLEADO ← Acceso operacional
└── Users:
    ├── admin.segar (ADMIN + EMPLEADO)
    └── empleado.segar (EMPLEADO)
```

### Uso en Angular
```typescript
// Verificar roles en componentes
export class UserManagementComponent {
  constructor(private auth: AuthService) {}
  
  ngOnInit() {
    if (this.auth.isAdmin()) {
      this.loadAdminFeatures();             // ← Solo para admins
    }
    
    if (this.auth.isEmpleado()) {
      this.loadEmployeeFeatures();         // ← Para empleados
    }
  }
}
```

## 📱 FLUJO COMPLETO DE UNA REQUEST PROTEGIDA

```
1. Usuario hace click en "Ver Usuarios" (requiere rol ADMIN)
2. Angular ejecuta: http.get('/api/admin/users')
3. AuthInterceptor agrega: Authorization: Bearer <jwt-token>
4. Request llega al backend: GET /api/admin/users
5. Spring Security intercepta y valida:
   ✅ Token válido y no expirado
   ✅ Usuario tiene rol ADMIN
6. Controller procesa la request
7. Respuesta regresa a Angular
8. Angular muestra los datos
```

## 🛠️ DEBUGGING Y TROUBLESHOOTING

### Verificar en Consola del Navegador
```javascript
// Ver estado de autenticación
console.log('Authenticated:', authService.isAuthenticated());
console.log('User:', authService.getUser());
console.log('Token:', authService.getToken().substring(0, 50) + '...');

// Ver información del token
authService.logTokenInfo();
```

### Verificar en Network Tab
- Buscar requests a `localhost:8090`
- Verificar header: `Authorization: Bearer eyJhbGc...`
- Status 200 = OK, Status 401 = No autenticado, Status 403 = Sin permisos

## 🎉 RESUMEN

Tu sistema de autenticación con Keycloak proporciona:

✅ **Single Sign-On (SSO)** - Un login para toda la aplicación
✅ **JWT Tokens** - Autenticación sin estado (stateless)
✅ **Role-Based Access Control (RBAC)** - Autorización por roles
✅ **Renovación automática** - Tokens se renuevan automáticamente
✅ **Interceptores automáticos** - Headers de autenticación automáticos
✅ **Guards de ruta** - Protección de páginas por roles
✅ **Validación centralizada** - Keycloak maneja toda la seguridad

**¡Es una solución enterprise-grade completa y robusta!** 🚀