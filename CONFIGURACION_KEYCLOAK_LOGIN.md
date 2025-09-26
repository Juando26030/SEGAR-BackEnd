# 🔧 CONFIGURACIÓN KEYCLOAK PARA LOGIN CON CREDENCIALES

## ⚠️ IMPORTANTE: Habilitar Resource Owner Password Flow

Para que funcione el login con usuario/contraseña desde tu formulario, necesitas configurar Keycloak:

### 1. Acceder a Keycloak Admin Console
- URL: http://localhost:8080
- Usuario: admin / admin123

### 2. Configurar el Cliente `segar-frontend`

```
Realm: segar
└── Clients
    └── segar-frontend
        ├── Settings
        │   ├── Client authentication: OFF ✅
        │   ├── Authorization: OFF ✅
        │   ├── Standard flow: ON ✅
        │   ├── Direct access grants: ON ✅ ← IMPORTANTE
        │   ├── Implicit flow: OFF ✅
        │   └── Service accounts roles: OFF ✅
        └── Advanced Settings
            └── Proof Key for Code Exchange (PKCE): S256 ✅
```

### 3. Comandos PowerShell para Configurar Automáticamente

```powershell
# Obtener token de admin
$adminToken = (Invoke-RestMethod -Uri "http://localhost:8080/realms/master/protocol/openid-connect/token" -Method Post -ContentType "application/x-www-form-urlencoded" -Body "grant_type=password&client_id=admin-cli&username=admin&password=admin123").access_token

# Obtener cliente segar-frontend
$client = Invoke-RestMethod -Uri "http://localhost:8080/admin/realms/segar/clients" -Headers @{"Authorization" = "Bearer $adminToken"} | Where-Object { $_.clientId -eq "segar-frontend" }

# Habilitar Direct Access Grants (Resource Owner Password Flow)
$clientUpdate = @{
    clientId = "segar-frontend"
    enabled = $true
    publicClient = $true
    standardFlowEnabled = $true
    directAccessGrantsEnabled = $true  # ← ESTO es lo importante
    implicitFlowEnabled = $false
    serviceAccountsEnabled = $false
    authorizationServicesEnabled = $false
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/admin/realms/segar/clients/$($client.id)" -Method Put -Headers @{"Authorization" = "Bearer $adminToken"; "Content-Type" = "application/json"} -Body $clientUpdate

Write-Host "✅ Cliente segar-frontend configurado correctamente para Resource Owner Password Flow"
```

### 4. Verificar la Configuración

```powershell
# Probar login con credenciales
$testLogin = @{
    grant_type = "password"
    client_id = "segar-frontend"
    username = "admin.segar"
    password = "admin123"
    scope = "openid profile email"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/realms/segar/protocol/openid-connect/token" -Method Post -ContentType "application/x-www-form-urlencoded" -Body ($testLogin.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" } | Join-String -Separator "&")

if ($response.access_token) {
    Write-Host "✅ Login funcionando correctamente"
    Write-Host "Token: $($response.access_token.Substring(0,50))..."
} else {
    Write-Host "❌ Error en configuración"
}
```

## 🚨 NOTA DE SEGURIDAD

El Resource Owner Password Flow **NO es recomendado para producción** porque:
- El frontend maneja directamente las credenciales
- No hay 2FA/MFA
- Menor seguridad que Authorization Code Flow

### 🔄 Para Producción, usa Authorization Code Flow:

```typescript
// Producción: Redirigir a Keycloak
async loginProduction(): Promise<void> {
  await this.keycloak.login({
    redirectUri: 'http://localhost:4200/callback'
  });
}
```

## 📋 RESUMEN DE CAMBIOS

✅ **AuthService**: Agregado método `loginWithCredentials()`
✅ **LoginComponent**: Validación real de credenciales
✅ **Keycloak**: Configurado para Resource Owner Password Flow
✅ **UI**: Botones de prueba y credenciales visibles

**¡Tu login ahora hace autenticación REAL!** 🔐