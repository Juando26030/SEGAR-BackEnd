#!/bin/bash

# Script para iniciar el Backend SEGAR con configuración de producción
# Archivo: ~/SEGAR-BackEnd/segar-backend/start-backend.sh

echo "========================================"
echo "Iniciando SEGAR Backend..."
echo "========================================"

# ⚠️ CAMBIAR estas URLs por tu dominio real
export KEYCLOAK_AUTH_SERVER_URL=https://api.tudominio.com/auth
export KEYCLOAK_ISSUER_URI=https://api.tudominio.com/auth/realms/segar
export KEYCLOAK_JWK_SET_URI=https://api.tudominio.com/auth/realms/segar/protocol/openid-connect/certs

# Directorio del proyecto
cd ~/SEGAR-BackEnd/segar-backend

# Buscar el archivo JAR
JAR_FILE=$(ls target/backend-*.jar 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ Error: No se encontró el archivo JAR en target/"
    echo "Por favor, compila el proyecto primero con: ./mvnw clean package"
    exit 1
fi

echo "✅ Archivo JAR encontrado: $JAR_FILE"
echo "📍 Configuración Keycloak: $KEYCLOAK_AUTH_SERVER_URL"
echo "🚀 Iniciando aplicación..."
echo ""

# Ejecutar el backend con las configuraciones
java -jar "$JAR_FILE" \
  --spring.security.oauth2.resourceserver.jwt.issuer-uri="$KEYCLOAK_ISSUER_URI" \
  --spring.security.oauth2.resourceserver.jwt.jwk-set-uri="$KEYCLOAK_JWK_SET_URI" \
  --keycloak.auth-server-url="$KEYCLOAK_AUTH_SERVER_URL"

