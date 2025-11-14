#!/bin/bash

# ==============================================================================
# Script de Inicio del Backend SEGAR - PRODUCCION
# ==============================================================================
# Este script inicia el backend Spring Boot con el perfil de producción
# y configuración HTTPS para trabajar detrás de Nginx
# ==============================================================================

set -e  # Detener en caso de error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Iniciando SEGAR Backend - PRODUCCIÓN${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Cargar variables de entorno desde .env.production si existe
ENV_FILE="$SCRIPT_DIR/.env.production"
if [ -f "$ENV_FILE" ]; then
    echo -e "${GREEN}✓${NC} Cargando variables de entorno desde: $ENV_FILE"
    # Exportar variables (ignorando comentarios y líneas vacías)
    set -a
    source <(grep -v '^#' "$ENV_FILE" | grep -v '^$')
    set +a
else
    echo -e "${YELLOW}⚠${NC} Archivo .env.production no encontrado"
    echo -e "${YELLOW}⚠${NC} Usando variables de entorno del sistema o valores por defecto"
fi

# Establecer perfil de Spring Boot
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-production}

# Verificar que las variables críticas estén configuradas
if [ -z "$KEYCLOAK_AUTH_SERVER_URL" ]; then
    echo -e "${YELLOW}⚠${NC} KEYCLOAK_AUTH_SERVER_URL no configurada, usando valor por defecto"
    export KEYCLOAK_AUTH_SERVER_URL="https://segar-solutions.duckdns.org/auth"
fi

if [ -z "$KEYCLOAK_ISSUER_URI" ]; then
    export KEYCLOAK_ISSUER_URI="${KEYCLOAK_AUTH_SERVER_URL}/realms/segar"
fi

if [ -z "$KEYCLOAK_JWK_SET_URI" ]; then
    export KEYCLOAK_JWK_SET_URI="${KEYCLOAK_AUTH_SERVER_URL}/realms/segar/protocol/openid-connect/certs"
fi

# Buscar el archivo JAR
JAR_FILE=$(find target -name "backend-*.jar" -type f 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}✗${NC} Error: No se encontró el archivo JAR en target/"
    echo ""
    echo "Por favor, compila el proyecto primero:"
    echo "  ${BLUE}./mvnw clean package -DskipTests${NC}"
    echo ""
    exit 1
fi

# Mostrar información de configuración
echo -e "${GREEN}✓${NC} Archivo JAR encontrado: ${YELLOW}$JAR_FILE${NC}"
echo -e "${GREEN}✓${NC} Perfil activo: ${YELLOW}$SPRING_PROFILES_ACTIVE${NC}"
echo -e "${GREEN}✓${NC} Puerto del servidor: ${YELLOW}${SERVER_PORT:-8090}${NC}"
echo -e "${GREEN}✓${NC} Keycloak Auth Server: ${YELLOW}$KEYCLOAK_AUTH_SERVER_URL${NC}"
echo -e "${GREEN}✓${NC} Keycloak Issuer URI: ${YELLOW}$KEYCLOAK_ISSUER_URI${NC}"
echo ""
echo -e "${BLUE}🚀 Iniciando aplicación...${NC}"
echo ""
echo -e "${YELLOW}Presiona Ctrl+C para detener el servidor${NC}"
echo ""

# Ejecutar el backend
exec java -jar "$JAR_FILE" \
  --spring.profiles.active="$SPRING_PROFILES_ACTIVE"

