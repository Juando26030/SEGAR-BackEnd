Write-Host "🚀 Iniciando Frontend Angular con Keycloak..." -ForegroundColor Green

# Cambiar al directorio del frontend
Set-Location "C:\Users\jonat\OneDrive\Documentos\GitHub\SEGAR-FrontEnd\segar-frontend"

# Verificar si estamos en el directorio correcto
if (Test-Path "angular.json") {
    Write-Host "✅ Directorio correcto encontrado" -ForegroundColor Green
    
    # Verificar si keycloak-js está instalado
    if (Test-Path "node_modules/keycloak-js") {
        Write-Host "✅ Keycloak-js ya está instalado" -ForegroundColor Green
    } else {
        Write-Host "📦 Instalando keycloak-js..." -ForegroundColor Yellow
        npm install keycloak-js@23.0.0
    }
    
    # Iniciar el servidor de desarrollo
    Write-Host "🔄 Iniciando servidor de desarrollo..." -ForegroundColor Cyan
    Write-Host "🌐 La aplicación estará disponible en: http://localhost:4200" -ForegroundColor Yellow
    Write-Host "🔑 Keycloak está configurado en: http://localhost:8080" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "📋 Credenciales de prueba:" -ForegroundColor Magenta
    Write-Host "   Admin: admin.segar / admin123" -ForegroundColor White
    Write-Host "   Empleado: empleado.segar / empleado123" -ForegroundColor White
    Write-Host ""
    
    ng serve --port 4200 --open
} else {
    Write-Host "❌ No se encontró angular.json. Verifica que estés en el directorio correcto." -ForegroundColor Red
    $currentDir = Get-Location
    Write-Host "Directorio actual: $currentDir" -ForegroundColor Yellow
}