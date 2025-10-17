# INSTRUCCIONES PARA ACTUALIZAR EL FRONTEND CON KEYCLOAK

## 1. Instalar dependencias

En la carpeta del frontend (`segar-frontend`), ejecuta:

```bash
npm install keycloak-js@23.0.0
```

## 2. Copiar archivos actualizados

### 2.1 Actualizar app.config.ts
Reemplaza el contenido de `src/app/app.config.ts` con el archivo `FRONTEND-UPDATES/app.config.ts`

### 2.2 Actualizar auth.service.ts  
Reemplaza el contenido de `src/app/auth/services/auth.service.ts` con el archivo `FRONTEND-UPDATES/auth.service.ts`

### 2.3 Actualizar auth.interceptor.ts
Reemplaza el contenido de `src/app/auth/interceptors/auth.interceptor.ts` con el archivo `FRONTEND-UPDATES/auth.interceptor.ts`

### 2.4 Actualizar auth.guard.ts
Reemplaza el contenido de `src/app/auth/guard/auth.guard.ts` con el archivo `FRONTEND-UPDATES/auth.guard.ts`

## 3. Comandos para copiar archivos (PowerShell)

```powershell
# Navegar al directorio del frontend
cd "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend"

# Instalar dependencias
npm install keycloak-js@23.0.0

# Copiar archivos (ejecutar desde el directorio SEGAR-BackEnd)
Copy-Item "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-BackEnd\FRONTEND-UPDATES\app.config.ts" "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend\src\app\app.config.ts" -Force

Copy-Item "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-BackEnd\FRONTEND-UPDATES\auth.service.ts" "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend\src\app\auth\services\auth.service.ts" -Force

Copy-Item "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-BackEnd\FRONTEND-UPDATES\auth.interceptor.ts" "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend\src\app\auth\interceptors\auth.interceptor.ts" -Force

Copy-Item "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-BackEnd\FRONTEND-UPDATES\auth.guard.ts" "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend\src\app\auth\guard\auth.guard.ts" -Force
```

## 4. Ejecutar y probar

```bash
# En el directorio del frontend
cd "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend"
ng serve

# En otra terminal, mantener el backend corriendo
cd "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-BackEnd\segar-backend"
.\mvnw spring-boot:run
```

## 5. Verificación

1. **Consola del navegador**: Debe aparecer "Keycloak initialized successfully"
2. **Redirección automática**: Te debe llevar a la página de login de Keycloak
3. **Después del login**: Debe aparecer la información del usuario en la consola
4. **Token en las requests**: Revisa las herramientas de desarrollador, las requests a localhost:8090 deben tener header Authorization

## 6. Comandos de prueba en consola del navegador

```javascript
// Verificar estado de autenticación
window.authService = document.querySelector('app-root')?._ngHost?.injector.get('AuthService');
console.log('Authenticated:', window.authService?.isAuthenticated());
console.log('User:', window.authService?.getUser());
console.log('Is Admin:', window.authService?.isAdmin());

// Ver información del token
window.authService?.logTokenInfo();
```

## ¡Listo! Tu frontend ahora está completamente integrado con Keycloak 🎉