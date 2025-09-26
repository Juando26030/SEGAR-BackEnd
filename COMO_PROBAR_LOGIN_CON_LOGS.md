# SCRIPT PARA PROBAR EL LOGIN CON LOGS DETALLADOS

## ¿Qué hice?

1. ✅ **AuthService**: Agregué logs detallados en `loginWithCredentials()`
2. ✅ **AuthService**: Agregué logs detallados en `loadUserProfile()` 
3. ✅ **AuthGuard**: Agregué logs para ver si está bloqueando el acceso

## Para probar ahora:

### 1. Inicia Angular:
```powershell
cd "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend"
npm start
# O si no funciona:
npx ng serve --port 4201
```

### 2. Abre el navegador:
- Ve a: http://localhost:4200/auth/login (o el puerto que use)
- Abre **DevTools** (F12) → **Console**

### 3. Haz la prueba:
1. Click en "**Test Empleado**"
2. Click en "**Iniciar Sesión**"
3. **MIRA LA CONSOLA** - verás logs como:

```
🔐 =================================
🔐 INICIANDO LOGIN CON CREDENCIALES
🔐 Usuario: empleado.segar
🔐 =================================
✅ Keycloak inicializado silenciosamente
📡 Respuesta de Keycloak status: 200
✅ TOKEN OBTENIDO EXITOSAMENTE
👤 =================================
👤 CARGANDO PERFIL DE USUARIO
👤 Roles extraídos de segar-backend: ["Empleado"]
✅ PERFIL DE USUARIO CARGADO CORRECTAMENTE
🛡️ =================================
🛡️ AUTH GUARD - VERIFICANDO ACCESO
🛡️ AuthService.isAuthenticated(): true/false
```

### 4. ¿Qué buscar en los logs?

**Si funciona correctamente:**
- ✅ Status 200 de Keycloak
- ✅ Token obtenido
- ✅ Roles extraídos: ["Empleado"]
- ✅ AuthGuard permite acceso
- ✅ Redirección a /main/panel

**Si NO funciona:**
- ❌ Status diferente de 200
- ❌ No se extraen roles
- ❌ AuthGuard deniega acceso
- ❌ isAuthenticated() = false

## ¿Por qué se borra la consola?

Cuando Angular redirige, la consola se limpia. Pero con estos logs detallados podrás ver exactamente en qué punto falla antes de que se borre.

**¡Ve la consola INMEDIATAMENTE después de hacer click en "Iniciar Sesión"!**