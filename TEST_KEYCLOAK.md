# Prueba de Integración con Keycloak - SEGAR

## ✅ Estado Actual de la Implementación

### Backend - COMPLETADO ✅
1. **Dependencias**: ✅ OAuth2 Resource Server agregado al pom.xml
2. **Configuración**: ✅ Keycloak configurado en application.properties
3. **Seguridad**: ✅ SecurityConfig actualizado con JWT y roles
4. **Controladores**: ✅ AuthController creado con endpoints protegidos
5. **DTOs**: ✅ UserInfoDTO creado para intercambio de datos

### Keycloak Server - COMPLETADO ✅
1. **Realm**: ✅ "segar" configurado
2. **Clientes**: ✅ segar-backend y segar-frontend creados
3. **Roles**: ✅ admin y empleado configurados
4. **Usuarios**: ✅ Usuarios de prueba creados con roles asignados

### Frontend - PARCIALMENTE COMPLETADO ⏳
1. **Dependencias**: ✅ keycloak-js instalado
2. **Servicios**: ✅ AuthService creado
3. **Guards**: ✅ AuthGuard configurado
4. **Interceptores**: ✅ AuthInterceptor configurado
5. **Configuración**: ⏳ Pendiente actualizar app.config.ts

## 🧪 Cómo Probar la Integración

### Paso 1: Verificar que los servicios están corriendo
- ✅ Keycloak: http://localhost:8080
- ✅ Backend: http://localhost:8090 (desde tu IDE)
- ⏳ Frontend: http://localhost:4200

### Paso 2: Probar endpoints públicos del backend
```bash
# Verificar que el backend está corriendo
curl http://localhost:8090/actuator/health

# Probar endpoint público
curl http://localhost:8090/h2-console
```

### Paso 3: Obtener token de Keycloak
```bash
# Obtener token para usuario admin
curl -X POST http://localhost:8080/realms/segar/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=segar-frontend" \
  -d "username=admin.segar" \
  -d "password=admin123"

# Obtener token para usuario empleado
curl -X POST http://localhost:8080/realms/segar/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=segar-frontend" \
  -d "username=empleado.segar" \
  -d "password=empleado123"
```

### Paso 4: Probar endpoints protegidos
```bash
# Reemplazar YOUR_TOKEN_HERE con el token obtenido del paso anterior

# Probar endpoint de información de usuario (ambos roles)
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8090/api/auth/user-info

# Probar endpoint de administrador (solo admin)
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8090/api/admin/users

# Probar endpoint de perfil (ambos roles)
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8090/api/usuarios/perfil
```

## 📋 Resultados Esperados

### Con token de ADMIN (admin.segar):
- ✅ `/api/auth/user-info` → Retorna información del usuario con roles ["admin"]
- ✅ `/api/admin/users` → Retorna "Lista de usuarios - Solo accesible para administradores"
- ✅ `/api/usuarios/perfil` → Retorna información del usuario

### Con token de EMPLEADO (empleado.segar):
- ✅ `/api/auth/user-info` → Retorna información del usuario con roles ["empleado"]  
- ❌ `/api/admin/users` → Error 403 (Forbidden)
- ✅ `/api/usuarios/perfil` → Retorna información del usuario

### Sin token:
- ❌ Todos los endpoints protegidos → Error 401 (Unauthorized)

## 🎯 Próximos Pasos para Completar

1. **Ejecutar el Backend**: Asegúrate de que esté corriendo desde tu IDE
2. **Probar la Autenticación**: Usar los comandos curl de arriba
3. **Configurar el Frontend**: Actualizar app.config.ts para usar Keycloak
4. **Probar la Integración Completa**: Frontend + Backend + Keycloak

## 🔧 Configuración del Frontend Pendiente

Necesitas actualizar tu `app.config.ts` con:

```typescript
import { ApplicationConfig, importProvidersFrom, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './auth/interceptors/auth.interceptor';
import { AuthService } from './auth/services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    AuthService,
    {
      provide: APP_INITIALIZER,
      useFactory: (authService: AuthService) => () => authService.initKeycloak(),
      deps: [AuthService],
      multi: true
    }
  ]
};
```

## 🎉 ¡La Implementación de Keycloak Está Casi Completa!

El backend ya está 100% configurado con Keycloak. Solo falta:
1. Probar que todo funciona correctamente
2. Completar la configuración del frontend
3. Verificar la integración end-to-end