# CREACIÓN DE USUARIOS EN KEYCLOAK - SEGAR

## Usuarios a crear:

1. **admin.segar** (Administrador)
   - Username: admin.segar
   - Password: admin123
   - Roles: ADMIN

2. **empleado.segar** (Empleado)
   - Username: empleado.segar
   - Password: empleado123
   - Roles: EMPLEADO

3. **admin.tramites** (Administrador de Trámites)
   - Username: admin.tramites
   - Password: admin123
   - Roles: ADMIN

## Pasos para crear usuarios:

### Opción 1: Por consola de administración de Keycloak

1. Ir a: http://localhost:8080/admin
2. Login: admin / admin
3. Seleccionar realm: segar
4. Ir a Users → Add user
5. Configurar cada usuario

### Opción 2: Por script PowerShell (automático)

```powershell
# Obtener token de administrador
$adminToken = (Invoke-RestMethod -Uri "http://localhost:8080/realms/master/protocol/openid-connect/token" -Method POST -Body @{
    client_id = "admin-cli"
    username = "admin"
    password = "admin"
    grant_type = "password"
}).access_token

# Crear usuario admin.segar
$user1 = @{
    username = "admin.segar"
    enabled = $true
    firstName = "Admin"
    lastName = "SEGAR"
    email = "admin@segar.com"
    credentials = @(@{
        type = "password"
        value = "admin123"
        temporary = $false
    })
} | ConvertTo-Json -Depth 3

Invoke-RestMethod -Uri "http://localhost:8080/admin/realms/segar/users" -Method POST -Headers @{"Authorization"="Bearer $adminToken"; "Content-Type"="application/json"} -Body $user1

# Crear usuario empleado.segar
$user2 = @{
    username = "empleado.segar"
    enabled = $true
    firstName = "Empleado"
    lastName = "SEGAR"
    email = "empleado@segar.com"
    credentials = @(@{
        type = "password"
        value = "empleado123"
        temporary = $false
    })
} | ConvertTo-Json -Depth 3

Invoke-RestMethod -Uri "http://localhost:8080/admin/realms/segar/users" -Method POST -Headers @{"Authorization"="Bearer $adminToken"; "Content-Type"="application/json"} -Body $user2

# Crear usuario admin.tramites
$user3 = @{
    username = "admin.tramites"
    enabled = $true
    firstName = "Admin"
    lastName = "Tramites"
    email = "admin.tramites@segar.com"
    credentials = @(@{
        type = "password"
        value = "admin123"
        temporary = $false
    })
} | ConvertTo-Json -Depth 3

Invoke-RestMethod -Uri "http://localhost:8080/admin/realms/segar/users" -Method POST -Headers @{"Authorization"="Bearer $adminToken"; "Content-Type"="application/json"} -Body $user3
```

## Asignar roles después de crear usuarios:

1. Obtener ID de cada usuario
2. Asignar roles correspondientes
3. Verificar en cliente segar-backend