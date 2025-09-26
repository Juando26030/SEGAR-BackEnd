# 🔐 AUTENTICACIÓN SIMPLE: CÓMO FUNCIONA CON TU LOGIN ACTUAL

## 📱 FLUJO PASO A PASO (EN TÉRMINOS SIMPLES)

### 🎬 **ESCENARIO**: Usuario quiere entrar al sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE AUTENTICACIÓN                       │
└─────────────────────────────────────────────────────────────────┘

1️⃣ USUARIO ABRE LA APLICACIÓN
   🌐 http://localhost:4200
   
2️⃣ ANGULAR PREGUNTA: "¿Tienes un token válido?"
   ❌ No → Redirige a LOGIN
   
3️⃣ USUARIO VE TU PANTALLA DE LOGIN
   📱 Componente: login-form.component
   
4️⃣ USUARIO INGRESA CREDENCIALES
   👤 Usuario: admin.segar
   🔑 Password: admin123
   
5️⃣ ANGULAR ENVÍA CREDENCIALES A KEYCLOAK
   🚀 POST a http://localhost:8080/realms/segar/protocol/openid-connect/token
   
6️⃣ KEYCLOAK VERIFICA LAS CREDENCIALES
   ✅ ¿Existe el usuario? SÍ
   ✅ ¿Password correcto? SÍ
   ✅ ¿Usuario activo? SÍ
   
7️⃣ KEYCLOAK RESPONDE CON TOKEN JWT
   📋 Token = "eyJhbGc...XYZ" (contiene roles del usuario)
   
8️⃣ ANGULAR GUARDA EL TOKEN
   💾 AuthService.setToken(token)
   
9️⃣ USUARIO YA ESTÁ AUTENTICADO
   🎉 Redirige al dashboard o página principal
```

## ⏰ **¿CUÁNDO SE VERIFICA LA AUTENTICACIÓN?**

### 🚪 **Momento 1: Al Intentar Acceder a una Página Protegida**
```typescript
// Tu ruta protegida (ejemplo: /dashboard)
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [AuthGuard]  // ← AQUÍ se verifica
}

// AuthGuard pregunta: "¿Este usuario está logueado?"
canActivate(): boolean {
  if (this.authService.isAuthenticated()) {
    return true;   // ✅ "Sí, puede pasar"
  } else {
    return false;  // ❌ "No, vete al login"
  }
}
```

### 🔍 **Momento 2: En Cada Request al Backend**
```typescript
// Interceptor automático (cada vez que llamas una API)
export const authInterceptor = (req, next) => {
  const token = authService.getToken();
  
  // Agrega el token automáticamente
  const authReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`)
  });
  
  return next(authReq);
};
```

### 🛡️ **Momento 3: En el Backend (Spring Boot)**
```java
// Cada endpoint protegido verifica automáticamente
@GetMapping("/api/tramites")
@PreAuthorize("hasRole('EMPLEADO')")  // ← AQUÍ se verifica el rol
public List<Tramite> getTramites() {
    // Spring pregunta: "¿Este token es válido? ¿Tiene rol EMPLEADO?"
    return tramiteService.findAll();
}
```

## 🎯 **INTEGRACIÓN CON TU LOGIN ACTUAL**

### 📝 **Tu Componente Login Actual**
```typescript
// login-form.component.ts (tu implementación actual)
export class LoginFormComponent {
  
  onLogin(credentials: LoginCredentials) {
    // EN VEZ de validar con tu backend...
    // this.authService.login(credentials) ❌
    
    // AHORA usas Keycloak:
    this.keycloakService.login(credentials) // ✅
      .then(token => {
        // Usuario autenticado exitosamente
        this.router.navigate(['/dashboard']);
      })
      .catch(error => {
        // Credenciales incorrectas
        this.showError('Usuario o contraseña incorrectos');
      });
  }
}
```

### 🔄 **Actualización Necesaria en tu AuthService**
```typescript
// auth.service.ts (lo que necesitas actualizar)
export class AuthService {
  
  // MÉTODO ANTERIOR (sin Keycloak)
  login(credentials): Observable<any> {
    return this.http.post('/api/auth/login', credentials);  // ❌ Ya no se usa
  }
  
  // MÉTODO NUEVO (con Keycloak)
  async loginWithKeycloak(username: string, password: string): Promise<boolean> {
    try {
      // Envía credenciales directamente a Keycloak
      const response = await fetch('http://localhost:8080/realms/segar/protocol/openid-connect/token', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          'grant_type': 'password',
          'client_id': 'segar-frontend',
          'username': username,
          'password': password
        })
      });
      
      if (response.ok) {
        const data = await response.json();
        this.saveToken(data.access_token);  // ✅ Guarda el token JWT
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  }
}
```

## 🎭 **¿QUÉ CAMBIA EN TU INTERFAZ?**

### 🎨 **Tu Login Form Sigue Igual Visualmente**
```html
<!-- login-form.component.html (NO cambia) -->
<form [formGroup]="loginForm" (ngSubmit)="onLogin()">
  <input formControlName="username" placeholder="Usuario">
  <input formControlName="password" type="password" placeholder="Contraseña">
  <button type="submit">Iniciar Sesión</button>
</form>
```

### 🔧 **Solo Cambia la Lógica Interna**
```typescript
// ANTES: Validaba con tu backend
onLogin() {
  this.authService.login(this.loginForm.value).subscribe(
    response => { /* manejar respuesta */ }
  );
}

// AHORA: Valida con Keycloak (pero se ve igual para el usuario)
onLogin() {
  const { username, password } = this.loginForm.value;
  this.authService.loginWithKeycloak(username, password).then(
    success => { 
      if (success) {
        this.router.navigate(['/dashboard']);
      } else {
        this.showError('Credenciales incorrectas');
      }
    }
  );
}
```

## 🎯 **VERIFICACIÓN EN TIEMPO REAL**

### 🔍 **¿Cuándo se Verifica que el Empleado Tiene Acceso?**

1. **Al hacer login** → Keycloak verifica credenciales
2. **Al navegar a páginas** → AuthGuard verifica token válido
3. **Al llamar APIs** → Backend verifica roles en el token
4. **Cada 5 minutos** → Token se renueva automáticamente

### 💡 **Ejemplo Práctico**

```typescript
// Usuario empleado.segar hace login
// 1. Keycloak genera token con roles: ["EMPLEADO"]

// 2. Usuario intenta acceder a /admin/usuarios
canActivate(): boolean {
  const user = this.authService.getUser();
  return user.roles.includes('ADMIN');  // ❌ false → Acceso denegado
}

// 3. Usuario accede a /tramites
canActivate(): boolean {
  const user = this.authService.getUser();
  return user.roles.includes('EMPLEADO');  // ✅ true → Acceso permitido
}
```

## 🚀 **RESUMEN SUPER SIMPLE**

1. **Usuario ve tu login** (igual que siempre)
2. **Ingresa credenciales** (usuario/password)
3. **Angular pregunta a Keycloak**: "¿Son válidas?"
4. **Keycloak responde**: "Sí, aquí tienes el token con sus roles"
5. **Angular guarda el token** y redirige al usuario
6. **Cada página/API verifica automáticamente** el token y roles

**👥 Para el usuario final: TODO SE VE IGUAL**
**🔧 Para el sistema: TODO ES MÁS SEGURO Y CENTRALIZADO**

**¡Tu login actual funciona igual, pero ahora es enterprise-grade!** 🎉