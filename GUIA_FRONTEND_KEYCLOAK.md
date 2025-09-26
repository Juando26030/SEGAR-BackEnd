# 🚀 Guía Completa: Actualización y Prueba del Frontend Angular

## 📝 Paso 1: Actualizar app.config.ts

Copia y pega este código en tu archivo `src/app/app.config.ts`:

```typescript
import { ApplicationConfig, APP_INITIALIZER, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { AuthService } from './auth/services/auth.service';
import { authInterceptor } from './auth/interceptors/auth.interceptor';

// Función para inicializar Keycloak
function initializeKeycloak(authService: AuthService) {
  return (): Promise<boolean> => {
    return authService.initKeycloak();
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),
    provideHttpClient(withInterceptors([authInterceptor])),
    AuthService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      deps: [AuthService],
      multi: true
    }
  ]
};
```

## 🔧 Paso 2: Verificar Dependencies en package.json

Asegúrate de que tienes keycloak-js instalado:

```bash
cd segar-frontend
npm install keycloak-js@23.0.0
```

## 🏃‍♂️ Paso 3: Ejecutar el Frontend

```bash
cd c:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend
npm start
# O si prefieres:
ng serve
```

El frontend debería ejecutarse en: **http://localhost:4200**

## 🧪 Cómo Probar que Está Funcionando

### 1. ✅ Verificar Inicialización de Keycloak

Abre el navegador en `http://localhost:4200` y abre las **Herramientas de Desarrollador (F12)**:

**Console Output Esperado:**
```
🔐 Keycloak initialized successfully
🎯 Keycloak config loaded: {realm: "segar", url: "http://localhost:8080"}
```

### 2. ✅ Probar Login Manual

En la consola del navegador, ejecuta:

```javascript
// Verificar si Keycloak está disponible
console.log('Keycloak instance:', window.keycloakInstance);

// Probar login
window.keycloakInstance?.login();
```

### 3. ✅ Verificar Estado de Autenticación

Después del login, ejecuta en la consola:

```javascript
// Verificar si está autenticado
console.log('Authenticated:', window.keycloakInstance?.authenticated);

// Ver información del usuario
console.log('User info:', window.keycloakInstance?.tokenParsed);

// Ver roles
console.log('Roles:', window.keycloakInstance?.resourceAccess);
```

### 4. ✅ Probar Llamadas a la API

En la consola del navegador:

```javascript
// Obtener token
const token = window.keycloakInstance?.token;
console.log('Token:', token?.substring(0, 50) + '...');

// Probar endpoint protegido
fetch('http://localhost:8090/api/auth/user-info', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('✅ API Response:', data))
.catch(error => console.error('❌ API Error:', error));
```

## 🔍 Verificaciones de Diagnóstico

### A. Verificar Network Tab
1. Abre F12 → Network
2. Recarga la página
3. Buscar llamadas a:
   - `http://localhost:8080/realms/segar/.well-known/openid-configuration`
   - `http://localhost:8080/realms/segar/protocol/openid-connect/certs`

### B. Verificar Local Storage
1. F12 → Application → Local Storage
2. Buscar claves relacionadas con Keycloak:
   - `kc-callback-*`
   - Token storage keys

### C. Verificar AuthService
Crear un componente de diagnóstico temporal:

```typescript
// En cualquier componente
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-keycloak-test',
  template: `
    <div>
      <h2>🔐 Keycloak Status</h2>
      <p>Authenticated: {{isAuthenticated}}</p>
      <p>User: {{username}}</p>
      <p>Roles: {{roles | json}}</p>
      <button (click)="login()" *ngIf="!isAuthenticated">Login</button>
      <button (click)="logout()" *ngIf="isAuthenticated">Logout</button>
      <button (click)="testAPI()">Test API</button>
    </div>
  `
})
export class KeycloakTestComponent implements OnInit {
  isAuthenticated = false;
  username = '';
  roles: string[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.checkAuth();
  }

  checkAuth() {
    this.isAuthenticated = this.authService.isAuthenticated();
    if (this.isAuthenticated) {
      this.username = this.authService.getUsername();
      this.roles = this.authService.getRoles();
    }
  }

  login() {
    this.authService.login();
  }

  logout() {
    this.authService.logout();
  }

  async testAPI() {
    try {
      // Aquí puedes probar llamadas a tu API
      console.log('Testing API...');
    } catch (error) {
      console.error('API Error:', error);
    }
  }
}
```

## 🎯 Resultados Esperados

### ✅ Funcionamiento Correcto
- No hay errores en la consola del navegador
- Keycloak se inicializa automáticamente al cargar la app
- El login redirecciona correctamente a Keycloak
- Después del login, el usuario es redirigido de vuelta a la app
- Las llamadas a la API incluyen automáticamente el token JWT
- Los roles se extraen correctamente del token

### ❌ Problemas Comunes y Soluciones

**Error: "Keycloak not initialized"**
```bash
# Verificar que Keycloak está corriendo
curl http://localhost:8080/realms/segar/.well-known/openid-configuration
```

**Error: CORS**
```bash
# Verificar que el backend tiene CORS configurado
# Ya está configurado en SecurityConfig.java
```

**Error: "Token expired"**
```javascript
// En la consola del navegador
window.keycloakInstance?.updateToken(30)
  .then(refreshed => console.log('Token refreshed:', refreshed));
```

## 🚀 Comandos Rápidos de Verificación

### Terminal 1 - Backend (ya corriendo)
```bash
# Backend ya está en: http://localhost:8090
```

### Terminal 2 - Frontend
```bash
cd c:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend
npm install keycloak-js@23.0.0
npm start
```

### Terminal 3 - Pruebas
```bash
# Verificar Keycloak
curl http://localhost:8080/realms/segar

# Verificar Frontend
curl http://localhost:4200

# Verificar Backend
curl http://localhost:8090/actuator/health
```

## 🎊 ¡Listo para Probar!

Una vez que hagas estos cambios y ejecutes el frontend, deberías tener:
1. ✅ Frontend corriendo en http://localhost:4200
2. ✅ Backend corriendo en http://localhost:8090  
3. ✅ Keycloak corriendo en http://localhost:8080
4. ✅ Integración completa funcionando

¡La integración estará 100% operativa! 🚀