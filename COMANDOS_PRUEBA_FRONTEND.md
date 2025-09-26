# 🚀 COMANDOS RÁPIDOS PARA PROBAR KEYCLOAK + FRONTEND

## ⚡ Pasos Rápidos de Implementación

### 1. 📁 Navegar al Frontend
```bash
cd "c:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend"
```

### 2. 📦 Instalar Keycloak
```bash
npm install keycloak-js@23.0.0
```

### 3. 🔄 Actualizar Archivos

**A. app.config.ts** (Copiar desde: `frontend-app.config.ts`)
**B. auth.service.ts** (Copiar desde: `frontend-auth.service-updated.ts`)  
**C. auth.interceptor.ts** (Copiar desde: `frontend-auth.interceptor-updated.ts`)

### 4. 🏃‍♂️ Ejecutar Frontend
```bash
ng serve
# O
npm start
```

## 🧪 Comandos de Prueba Inmediata

### A. Verificar Servicios Corriendo
```powershell
# Keycloak (debe devolver configuración JSON)
curl http://localhost:8080/realms/segar/.well-known/openid-configuration

# Backend (debe devolver 401 sin token)
curl http://localhost:8090/api/auth/user-info

# Frontend (debe cargar la aplicación)
curl http://localhost:4200
```

### B. Prueba Completa de Autenticación
```powershell
# 1. Obtener token de Keycloak
$response = Invoke-RestMethod -Uri "http://localhost:8080/realms/segar/protocol/openid-connect/token" -Method Post -ContentType "application/x-www-form-urlencoded" -Body "grant_type=password&client_id=segar-frontend&username=admin.segar&password=admin123"

# 2. Extraer token
$token = $response.access_token

# 3. Probar endpoint protegido
Invoke-RestMethod -Uri "http://localhost:8090/api/auth/user-info" -Method Get -Headers @{"Authorization" = "Bearer $token"}

# 4. Probar endpoint de admin
Invoke-RestMethod -Uri "http://localhost:8090/api/admin/users" -Method Get -Headers @{"Authorization" = "Bearer $token"}
```

### C. Prueba desde el Navegador
1. Abre: **http://localhost:4200**
2. Abre **F12 → Console**
3. Ejecuta:
```javascript
// Verificar Keycloak
console.log('Keycloak:', window.keycloakInstance);

// Login manual
window.keycloakInstance?.login();

// Después del login, probar API
fetch('http://localhost:8090/api/auth/user-info', {
  headers: {
    'Authorization': `Bearer ${window.keycloakInstance?.token}`
  }
}).then(r => r.json()).then(console.log);
```

## ✅ Resultados Esperados

### Console del Navegador:
```
🔐 Keycloak initialized successfully {authenticated: true, realm: "segar", clientId: "segar-frontend"}
🔐 Adding auth token to request: http://localhost:8090/api/auth/user-info
```

### Respuesta de la API:
```json
{
  "username": "admin.segar",
  "email": "admin@segar.gov.co", 
  "firstName": "Administrador",
  "lastName": "SEGAR",
  "roles": ["admin"],
  "enabled": true
}
```

## 🆘 Solución de Problemas Rápida

### Error: "Module not found"
```bash
npm install keycloak-js@23.0.0
npm install
```

### Error: CORS
Verificar que el backend tenga CORS configurado (ya está listo).

### Error: "Keycloak not initialized"
Verificar que Keycloak esté corriendo:
```bash
docker ps  # Si usas Docker
# O verificar proceso de Java con Keycloak
```

### Error: "Invalid credentials"
Verificar usuarios en Keycloak Admin Console:
- URL: http://localhost:8080/admin
- Usuario: admin / Contraseña: admin

## 🎯 Flujo de Prueba Completa

1. **Servicios**: ✅ Keycloak (8080) + Backend (8090) + Frontend (4200)
2. **Login**: ✅ Click en Login → Redirección a Keycloak → Vuelta a Angular
3. **API**: ✅ Llamadas automáticas con JWT token
4. **Roles**: ✅ Acceso diferenciado por rol (admin vs empleado)
5. **Logout**: ✅ Cierre de sesión completo

## 🎊 ¡Listo para Usar!

Una vez completados estos pasos, tendrás una integración completa de:
- 🔐 **Keycloak** para autenticación
- 🛡️ **Spring Boot** para autorización  
- 🌐 **Angular** para interfaz de usuario
- 🚀 **JWT** para seguridad stateless

¡Todo funcionando en armonía! 🎉