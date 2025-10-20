# Guía de Despliegue: Configurar Nginx con HTTPS para Backend en Google Cloud

## Situación Actual
- **Backend**: Corriendo en puerto 8090 (HTTP)
- **Keycloak**: Corriendo en Docker en puerto 8080 (HTTP)
- **Problema**: Vercel requiere HTTPS para las peticiones
- **Solución**: Nginx como reverse proxy con certificado SSL/TLS

---

## PASO 1: Conectar a la Máquina Virtual

```bash
# Ya estás conectado como:
# segarsolutions@segar-server:~$
```

---

## PASO 2: Instalar Nginx

```bash
# Actualizar repositorios
sudo apt update

# Instalar Nginx
sudo apt install nginx -y

# Verificar que Nginx esté corriendo
sudo systemctl status nginx

# Habilitar Nginx para que inicie automáticamente
sudo systemctl enable nginx
```

---

## PASO 3: Instalar Certbot para Certificados SSL (Let's Encrypt)

```bash
# Instalar Certbot y el plugin de Nginx
sudo apt install certbot python3-certbot-nginx -y
```

---

## PASO 4: Configurar el Firewall (si está activo)

```bash
# Verificar si ufw está activo
sudo ufw status

# Si está activo, permitir tráfico HTTP y HTTPS
sudo ufw allow 'Nginx Full'
sudo ufw allow 8090/tcp
sudo ufw allow 8080/tcp
```

**IMPORTANTE**: También debes configurar las reglas de firewall en Google Cloud Console:
1. Ve a VPC Network > Firewall
2. Asegúrate de tener reglas que permitan:
   - Puerto 80 (HTTP)
   - Puerto 443 (HTTPS)
   - Puerto 8090 (Backend - solo desde localhost)
   - Puerto 8080 (Keycloak - solo desde localhost)

---

## PASO 5: Crear Configuración de Nginx

**Necesitas un dominio apuntando a tu IP de Google Cloud. Si no tienes dominio, puedes:**
- Usar servicios gratuitos como: FreeDNS, No-IP, DuckDNS
- Comprar un dominio económico (.online, .site, etc)

Para este ejemplo, asumiremos que tu dominio es: **`api.tudominio.com`**

```bash
# Crear archivo de configuración de Nginx
sudo nano /etc/nginx/sites-available/segar-backend
```

### Contenido del archivo `/etc/nginx/sites-available/segar-backend`:

```nginx
# Redirigir HTTP a HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name api.tudominio.com;  # CAMBIAR por tu dominio real

    # Permitir que Certbot valide el dominio
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirigir todo el tráfico a HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

# Configuración HTTPS
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name api.tudominio.com;  # CAMBIAR por tu dominio real

    # Certificados SSL (Certbot los configurará automáticamente)
    # ssl_certificate /etc/letsencrypt/live/api.tudominio.com/fullchain.pem;
    # ssl_certificate_key /etc/letsencrypt/live/api.tudominio.com/privkey.pem;

    # Configuraciones SSL recomendadas
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_session_timeout 10m;
    ssl_session_cache shared:SSL:10m;

    # Tamaño máximo de archivos (para uploads)
    client_max_body_size 25M;

    # Headers de seguridad
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Logs
    access_log /var/log/nginx/segar-backend-access.log;
    error_log /var/log/nginx/segar-backend-error.log;

    # Proxy para el Backend Spring Boot
    location /api/ {
        proxy_pass http://localhost:8090/;
        proxy_http_version 1.1;
        
        # Headers importantes para que Spring Boot detecte HTTPS
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # WebSocket support (si lo necesitas)
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Proxy para Keycloak
    location /auth/ {
        proxy_pass http://localhost:8080/;
        proxy_http_version 1.1;
        
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://localhost:8090/actuator/health;
        proxy_set_header Host $host;
        access_log off;
    }
}
```

---

## PASO 6: Activar la Configuración

```bash
# Crear enlace simbólico para activar el sitio
sudo ln -s /etc/nginx/sites-available/segar-backend /etc/nginx/sites-enabled/

# Eliminar configuración por defecto (opcional)
sudo rm /etc/nginx/sites-enabled/default

# Verificar que la configuración sea válida
sudo nginx -t

# Si todo está OK, recargar Nginx
sudo systemctl reload nginx
```

---

## PASO 7: Obtener Certificado SSL con Let's Encrypt

**IMPORTANTE**: Antes de ejecutar esto, asegúrate de que tu dominio esté apuntando a la IP pública de tu máquina virtual.

```bash
# Obtener certificado SSL (CAMBIAR api.tudominio.com por tu dominio real)
sudo certbot --nginx -d api.tudominio.com

# Durante el proceso te preguntará:
# 1. Tu email (para notificaciones de renovación)
# 2. Aceptar términos de servicio
# 3. Si quieres compartir tu email con EFF (opcional)

# Certbot configurará automáticamente Nginx con el certificado
```

---

## PASO 8: Configurar Renovación Automática del Certificado

```bash
# Verificar que el timer de renovación esté activo
sudo systemctl status certbot.timer

# Probar la renovación (dry-run, no renueva realmente)
sudo certbot renew --dry-run

# Si todo funciona bien, la renovación será automática cada 60 días
```

---

## PASO 9: Ajustar el Backend para Producción

### Opción A: Variables de Entorno (RECOMENDADO)

Crea un archivo de script para ejecutar tu backend:

```bash
# Crear script de inicio
nano ~/SEGAR-BackEnd/segar-backend/start-backend.sh
```

Contenido del archivo `start-backend.sh`:

```bash
#!/bin/bash

# Variables de entorno para producción
export KEYCLOAK_AUTH_SERVER_URL=https://api.tudominio.com/auth
export KEYCLOAK_ISSUER_URI=https://api.tudominio.com/auth/realms/segar
export KEYCLOAK_JWK_SET_URI=https://api.tudominio.com/auth/realms/segar/protocol/openid-connect/certs

# Ejecutar el backend
cd ~/SEGAR-BackEnd/segar-backend
java -jar target/backend-*.jar \
  --spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI} \
  --spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI} \
  --keycloak.auth-server-url=${KEYCLOAK_AUTH_SERVER_URL}
```

Dar permisos de ejecución:

```bash
chmod +x ~/SEGAR-BackEnd/segar-backend/start-backend.sh
```

### Opción B: Archivo application-production.properties

En tu proyecto local, puedes crear este archivo y subirlo al servidor.

---

## PASO 10: Reiniciar Backend y Keycloak

```bash
# Detener el backend actual (si está corriendo)
# Presiona Ctrl+C si está en terminal, o encuentra el proceso:
ps aux | grep java
# kill -9 [PID del proceso java]

# Iniciar el backend con el nuevo script
~/SEGAR-BackEnd/segar-backend/start-backend.sh

# Para ejecutarlo en segundo plano:
nohup ~/SEGAR-BackEnd/segar-backend/start-backend.sh > ~/backend.log 2>&1 &

# Ver logs en tiempo real
tail -f ~/backend.log
```

---

## PASO 11: Configurar Keycloak para HTTPS

Si Keycloak está en Docker, necesitas configurarlo para que acepte HTTPS del proxy:

```bash
# Detener el contenedor actual de Keycloak
docker stop keycloak
docker rm keycloak

# Iniciar Keycloak con configuración de proxy
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -e KC_PROXY=edge \
  -e KC_HOSTNAME_STRICT=false \
  -e KC_HTTP_ENABLED=true \
  quay.io/keycloak/keycloak:latest start-dev
```

---

## PASO 12: Actualizar URLs en Keycloak Admin Console

1. Accede a Keycloak: `https://api.tudominio.com/auth`
2. Login como admin
3. Ve al realm "segar"
4. **Clients** > **segar-backend**:
   - Valid Redirect URIs: `https://tu-app.vercel.app/*`
   - Web Origins: `https://tu-app.vercel.app`
5. **Clients** > **segar-frontend** (si existe):
   - Valid Redirect URIs: `https://tu-app.vercel.app/*`
   - Web Origins: `https://tu-app.vercel.app`

---

## PASO 13: Actualizar Frontend en Vercel

En las variables de entorno de Vercel, actualiza:

```
NEXT_PUBLIC_API_URL=https://api.tudominio.com/api
NEXT_PUBLIC_KEYCLOAK_URL=https://api.tudominio.com/auth
NEXT_PUBLIC_KEYCLOAK_REALM=segar
NEXT_PUBLIC_KEYCLOAK_CLIENT_ID=segar-frontend
```

---

## PASO 14: Verificar que Todo Funcione

```bash
# Test 1: Verificar Nginx
curl https://api.tudominio.com/health

# Test 2: Verificar Backend
curl https://api.tudominio.com/api/actuator/health

# Test 3: Verificar Keycloak
curl https://api.tudominio.com/auth/realms/segar

# Ver logs de Nginx
sudo tail -f /var/log/nginx/segar-backend-error.log
sudo tail -f /var/log/nginx/segar-backend-access.log

# Ver logs del Backend
tail -f ~/backend.log
```

---

## PASO 15: Configurar como Servicio Systemd (OPCIONAL pero RECOMENDADO)

Para que el backend se inicie automáticamente al reiniciar el servidor:

```bash
# Crear archivo de servicio
sudo nano /etc/systemd/system/segar-backend.service
```

Contenido:

```ini
[Unit]
Description=SEGAR Backend Spring Boot Application
After=network.target

[Service]
Type=simple
User=segarsolutions
WorkingDirectory=/home/segarsolutions/SEGAR-BackEnd/segar-backend
ExecStart=/home/segarsolutions/SEGAR-BackEnd/segar-backend/start-backend.sh
Restart=always
RestartSec=10
StandardOutput=append:/home/segarsolutions/backend.log
StandardError=append:/home/segarsolutions/backend.log

Environment="KEYCLOAK_AUTH_SERVER_URL=https://api.tudominio.com/auth"
Environment="KEYCLOAK_ISSUER_URI=https://api.tudominio.com/auth/realms/segar"
Environment="KEYCLOAK_JWK_SET_URI=https://api.tudominio.com/auth/realms/segar/protocol/openid-connect/certs"

[Install]
WantedBy=multi-user.target
```

Activar el servicio:

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar el servicio
sudo systemctl enable segar-backend

# Iniciar el servicio
sudo systemctl start segar-backend

# Ver estado
sudo systemctl status segar-backend

# Ver logs
sudo journalctl -u segar-backend -f
```

---

## Comandos Útiles

```bash
# Reiniciar Nginx
sudo systemctl restart nginx

# Reiniciar Backend
sudo systemctl restart segar-backend

# Ver logs de Nginx
sudo tail -f /var/log/nginx/segar-backend-error.log

# Ver logs del Backend
tail -f ~/backend.log
# o si usas systemd:
sudo journalctl -u segar-backend -f

# Verificar certificado SSL
sudo certbot certificates

# Renovar certificado manualmente
sudo certbot renew

# Verificar procesos corriendo
ps aux | grep java
docker ps
```

---

## Troubleshooting

### Error: "502 Bad Gateway"
```bash
# Verificar que el backend esté corriendo
curl http://localhost:8090/actuator/health

# Verificar logs de Nginx
sudo tail -f /var/log/nginx/segar-backend-error.log
```

### Error: Certificado SSL no válido
```bash
# Verificar configuración DNS
nslookup api.tudominio.com

# Renovar certificado
sudo certbot renew --force-renewal
```

### Backend no inicia
```bash
# Ver logs completos
tail -100 ~/backend.log

# Verificar puerto en uso
sudo netstat -tulpn | grep 8090
```

---

## Resumen de URLs Finales

| Servicio | URL Local | URL Pública |
|----------|-----------|-------------|
| Backend API | http://localhost:8090 | https://api.tudominio.com/api |
| Keycloak | http://localhost:8080 | https://api.tudominio.com/auth |
| Health Check | http://localhost:8090/actuator/health | https://api.tudominio.com/health |

---

## Checklist Final

- [ ] Dominio apuntando a IP de Google Cloud
- [ ] Nginx instalado y configurado
- [ ] Certificado SSL obtenido con Let's Encrypt
- [ ] Backend corriendo con variables de entorno correctas
- [ ] Keycloak configurado para funcionar con proxy
- [ ] URLs actualizadas en Keycloak Admin Console
- [ ] Variables de entorno actualizadas en Vercel
- [ ] Firewall de Google Cloud configurado (puertos 80, 443)
- [ ] Pruebas exitosas desde Vercel

---

**¡IMPORTANTE!**: Reemplaza `api.tudominio.com` con tu dominio real en todos los archivos de configuración.

