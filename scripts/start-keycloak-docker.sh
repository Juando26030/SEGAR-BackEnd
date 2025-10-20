#!/bin/bash

# Script para iniciar Keycloak con Docker configurado para trabajar detrás de Nginx
# Archivo: ~/keycloak-config/start-keycloak-docker.sh

echo "========================================"
echo "Iniciando Keycloak con Docker..."
echo "========================================"

# Detener y eliminar contenedor anterior si existe
if [ "$(docker ps -aq -f name=keycloak)" ]; then
    echo "🔄 Deteniendo contenedor anterior..."
    docker stop keycloak 2>/dev/null
    docker rm keycloak 2>/dev/null
fi

# Iniciar Keycloak con configuración de proxy
echo "🚀 Iniciando nuevo contenedor Keycloak..."

docker run -d \
  --name keycloak \
  --restart unless-stopped \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -e KC_PROXY=edge \
  -e KC_HOSTNAME_STRICT=false \
  -e KC_HTTP_ENABLED=true \
  -e KC_PROXY_HEADERS=xforwarded \
  -v ~/keycloak-config/data:/opt/keycloak/data \
  quay.io/keycloak/keycloak:latest start-dev

echo ""
echo "✅ Keycloak iniciado correctamente"
echo "📍 Acceso local: http://localhost:8080"
echo "📍 Acceso público (cuando Nginx esté configurado): https://api.tudominio.com/auth"
echo ""
echo "Para ver logs: docker logs -f keycloak"

