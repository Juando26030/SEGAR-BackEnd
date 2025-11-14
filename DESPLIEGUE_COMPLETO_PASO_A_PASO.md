# ==============================================================================
# GUÍA DE DESPLIEGUE PASO A PASO - SEGAR Backend con HTTPS
# ==============================================================================
# Fecha: 2025-10-20
# Dominio: segar-solutions.duckdns.org
# ==============================================================================

## ✅ PRERREQUISITOS COMPLETADOS

- [x] Nginx instalado y configurado
- [x] Certificado SSL de Let's Encrypt obtenido
- [x] DuckDNS configurado con el dominio: segar-solutions.duckdns.org
- [x] Archivo de configuración de producción creado

---

## 📋 PARTE 1: CAMBIOS EN EL CÓDIGO (TU PC - Windows)

### Archivos Creados/Modificados:

1. ✅ `src/main/resources/application-production.properties` - Configuración de producción
2. ✅ `.env.production` - Variables de entorno (NO subir a Git)
3. ✅ `.env.example` - Plantilla de variables de entorno
4. ✅ `.gitignore` - Actualizado para proteger archivos sensibles
5. ✅ `scripts/start-backend-production.sh` - Script de inicio mejorado
6. ✅ `scripts/segar-backend-production.service` - Servicio systemd

---

## 🚀 PASO 1: COMPILAR EL PROYECTO

Abre PowerShell o CMD en Windows:

```cmd
cd C:\JDRJ\Javeriana\Tesis\Backend\SEGAR-BackEnd\segar-backend

# Limpiar y compilar
mvnw.cmd clean package -DskipTests
```

**Verifica que el JAR se haya generado:**
```cmd
dir target\backend-*.jar
```

---

## 📤 PASO 2: SUBIR ARCHIVOS AL SERVIDOR

### Opción A: Usar Git (RECOMENDADO)

```cmd
cd C:\JDRJ\Javeriana\Tesis\Backend\SEGAR-BackEnd

# Ver cambios
git status

# Agregar archivos (NO incluye .env.production por el .gitignore)
git add .

# Commit
git commit -m "feat: add production configuration with HTTPS support

- Add application-production.properties with environment variables
- Add .env.example template
- Update .gitignore to protect sensitive files
- Improve start-backend-production.sh with auto-loading env vars
- Update systemd service configuration"

# Push al repositorio
git push origin main
```

### Opción B: Usar SCP/SFTP (si no usas Git)

Con WinSCP o FileZilla, sube estos archivos:
- `segar-backend/src/main/resources/application-production.properties`
- `segar-backend/target/backend-*.jar`
- `scripts/start-backend-production.sh`
- `scripts/segar-backend-production.service`

---

## 🐧 PASO 3: EN EL SERVIDOR - CONFIGURACIÓN INICIAL

Conéctate por SSH:

```bash
ssh segarsolutions@34.171.23.243
```

### 3.1 Actualizar el Código (si usaste Git)

```bash
cd ~/SEGAR-BackEnd
git pull origin main
```

### 3.2 Compilar en el Servidor (si es necesario)

```bash
cd ~/SEGAR-BackEnd/segar-backend

# Compilar
./mvnw clean package -DskipTests

# Verificar que el JAR existe
ls -lh target/backend-*.jar
```

### 3.3 Crear Estructura de Directorios

```bash
# Crear directorios necesarios
mkdir -p ~/logs
mkdir -p ~/keycloak-config/data
mkdir -p ~/SEGAR-BackEnd/scripts
```

### 3.4 Copiar Scripts de Inicio

```bash
# Copiar el script de inicio (si no está)
cp ~/SEGAR-BackEnd/scripts/start-backend-production.sh ~/SEGAR-BackEnd/segar-backend/

# Dar permisos de ejecución
chmod +x ~/SEGAR-BackEnd/segar-backend/start-backend-production.sh
chmod +x ~/SEGAR-BackEnd/scripts/start-backend-production.sh
```

### 3.5 Crear Archivo de Variables de Entorno

```bash
# Crear el archivo .env.production
nano ~/SEGAR-BackEnd/segar-backend/.env.production
```

**Pega este contenido:**

```bash
# Variables de Entorno - Producción
SERVER_PORT=8090
SPRING_PROFILES_ACTIVE=production

# Keycloak Configuration
KEYCLOAK_AUTH_SERVER_URL=https://segar-solutions.duckdns.org/auth
KEYCLOAK_ISSUER_URI=https://segar-solutions.duckdns.org/auth/realms/segar
KEYCLOAK_JWK_SET_URI=https://segar-solutions.duckdns.org/auth/realms/segar/protocol/openid-connect/certs
KEYCLOAK_REALM=segar
KEYCLOAK_CLIENT_ID=segar-backend
KEYCLOAK_CLIENT_SECRET=BZp1DSBtfnzfxVuWTQ8SxyZ3SZUY3qo6

# Email Configuration (opcional - tiene valores por defecto)
EMAIL_SYNC_ENABLED=true
EMAIL_SYNC_INTERVAL=300000
EMAIL_SYNC_ON_STARTUP=true

# GCP
GCP_ENABLED=false

# Admin Credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
```

**Guardar:** `Ctrl+X`, `Y`, `Enter`

**Proteger el archivo:**
```bash
chmod 600 ~/SEGAR-BackEnd/segar-backend/.env.production
```

---

## 🔑 PASO 4: REINICIAR KEYCLOAK CON PROXY

```bash
# Detener contenedor actual
docker stop keycloak 2>/dev/null || true
docker rm keycloak 2>/dev/null || true

# Iniciar Keycloak con configuración de proxy
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

# Verificar que está corriendo
docker ps | grep keycloak

# Ver logs (opcional)
docker logs keycloak
```

**Espera 30 segundos** para que Keycloak inicie completamente.

---

## 🎯 PASO 5: PROBAR EL BACKEND MANUALMENTE (Recomendado)

Antes de configurar el servicio systemd, prueba que todo funciona:

```bash
# Ir al directorio del backend
cd ~/SEGAR-BackEnd/segar-backend

# Ejecutar manualmente para ver logs
./start-backend-production.sh
```

**Busca estos mensajes en la consola:**
- ✅ `Cargando variables de entorno desde: .env.production`
- ✅ `Perfil activo: production`
- ✅ `Keycloak Auth Server: https://segar-solutions.duckdns.org/auth`
- ✅ `Started BackendApplication in X seconds`

**Si ves errores:**
- Verifica que Keycloak esté corriendo: `docker ps | grep keycloak`
- Verifica logs de Keycloak: `docker logs keycloak`
- Verifica las URLs en `.env.production`

**Una vez que veas que inicia correctamente, presiona `Ctrl+C` para detenerlo.**

---

## ⚙️ PASO 6: CONFIGURAR SERVICIO SYSTEMD (Inicio Automático)

### 6.1 Crear el Archivo de Servicio

```bash
sudo nano /etc/systemd/system/segar-backend.service
```

**Pega este contenido:**

```ini
[Unit]
Description=SEGAR Backend Spring Boot Application (Production)
After=network.target docker.service
Wants=docker.service

[Service]
Type=simple
User=segarsolutions
Group=segarsolutions
WorkingDirectory=/home/segarsolutions/SEGAR-BackEnd/segar-backend

# Cargar variables de entorno desde archivo
EnvironmentFile=-/home/segarsolutions/SEGAR-BackEnd/segar-backend/.env.production

# Variable crítica como respaldo
Environment="SPRING_PROFILES_ACTIVE=production"

# Comando de ejecución
ExecStart=/home/segarsolutions/SEGAR-BackEnd/segar-backend/start-backend-production.sh

# Política de reinicio
Restart=always
RestartSec=10
StartLimitInterval=200
StartLimitBurst=5

# Logs
StandardOutput=append:/home/segarsolutions/logs/backend.log
StandardError=append:/home/segarsolutions/logs/backend-error.log

[Install]
WantedBy=multi-user.target
```

**Guardar:** `Ctrl+X`, `Y`, `Enter`

### 6.2 Activar e Iniciar el Servicio

```bash
# Recargar configuración de systemd
sudo systemctl daemon-reload

# Habilitar para inicio automático al arrancar el servidor
sudo systemctl enable segar-backend

# Iniciar el servicio
sudo systemctl start segar-backend

# Verificar estado
sudo systemctl status segar-backend
```

**Deberías ver:**
- `Active: active (running)`
- Sin errores en rojo

### 6.3 Ver Logs en Tiempo Real

```bash
# Logs del servicio
sudo journalctl -u segar-backend -f

# O ver el archivo de log directamente
tail -f ~/logs/backend.log
```

**Presiona `Ctrl+C` para salir**

---

## ✅ PASO 7: VERIFICAR QUE TODO FUNCIONA

### 7.1 Verificar desde el Servidor

```bash
# 1. Health check del backend
curl -k https://segar-solutions.duckdns.org/health

# 2. Health check del actuator
curl -k https://segar-solutions.duckdns.org/api/actuator/health

# 3. Verificar Keycloak
curl -k https://segar-solutions.duckdns.org/auth/realms/segar

# 4. Ver estado de todos los servicios
sudo systemctl status nginx
sudo systemctl status segar-backend
docker ps
```

### 7.2 Verificar desde tu Navegador

Abre estos enlaces:

1. **Backend Health**: https://segar-solutions.duckdns.org/health
   - Debe mostrar un mensaje o JSON

2. **Backend Actuator**: https://segar-solutions.duckdns.org/api/actuator/health
   - Debe mostrar: `{"status":"UP"}`

3. **Keycloak**: https://segar-solutions.duckdns.org/auth
   - Debe mostrar la página de Keycloak

4. **Keycloak Admin**: https://segar-solutions.duckdns.org/auth/admin
   - Login: `admin` / `admin`

---

## 🔧 PASO 8: CONFIGURAR KEYCLOAK ADMIN CONSOLE

1. Ve a: **https://segar-solutions.duckdns.org/auth/admin**
2. Login: `admin` / `admin`
3. Selecciona el realm **"segar"** (arriba a la izquierda)

### 8.1 Configurar Cliente Backend

4. Ve a **Clients** en el menú lateral
5. Busca y haz clic en **"segar-backend"**
6. Configura:
   - **Root URL**: `https://segar-solutions.duckdns.org`
   - **Valid Redirect URIs**: 
     - `https://segar-solutions.duckdns.org/*`
     - `https://tu-app-vercel.vercel.app/*` (tu app en Vercel)
   - **Valid post logout redirect URIs**: `+`
   - **Web Origins**: 
     - `https://segar-solutions.duckdns.org`
     - `https://tu-app-vercel.vercel.app`
   - O simplemente pon `*` en Web Origins si tienes problemas de CORS
7. Clic en **Save**

### 8.2 Configurar Cliente Frontend (si existe)

8. Busca el cliente **"segar-frontend"**
9. Configura:
   - **Root URL**: `https://tu-app-vercel.vercel.app`
   - **Valid Redirect URIs**: `https://tu-app-vercel.vercel.app/*`
   - **Valid post logout redirect URIs**: `https://tu-app-vercel.vercel.app/*`
   - **Web Origins**: `https://tu-app-vercel.vercel.app` o `*`
10. Clic en **Save**

---

## 🌐 PASO 9: ACTUALIZAR VERCEL

### 9.1 Configurar Variables de Entorno

1. Ve a tu proyecto en **Vercel Dashboard**
2. Navega a **Settings** → **Environment Variables**
3. Agrega/actualiza estas variables:

| Variable | Valor |
|----------|-------|
| `NEXT_PUBLIC_API_URL` | `https://segar-solutions.duckdns.org/api` |
| `NEXT_PUBLIC_KEYCLOAK_URL` | `https://segar-solutions.duckdns.org/auth` |
| `NEXT_PUBLIC_KEYCLOAK_REALM` | `segar` |
| `NEXT_PUBLIC_KEYCLOAK_CLIENT_ID` | `segar-frontend` |

4. Asegúrate de aplicar las variables a todos los entornos (Production, Preview, Development)

### 9.2 Redeploy la Aplicación

5. Ve a **Deployments**
6. Busca el último deployment
7. Click en el menú `⋯` → **Redeploy**
8. Espera a que termine el despliegue

---

## 🎉 PASO 10: PRUEBA FINAL

### Desde tu Aplicación Vercel:

1. Abre tu aplicación: `https://tu-app-vercel.vercel.app`
2. Intenta hacer login
3. Verifica que puedas:
   - ✅ Autenticarte con Keycloak
   - ✅ Hacer peticiones al backend
   - ✅ Ver datos correctamente

### Verificar en Navegador (F12 Console):

- ✅ No debe haber errores de CORS
- ✅ No debe haber errores de "Mixed Content" (HTTP/HTTPS)
- ✅ Las peticiones deben ir a `https://segar-solutions.duckdns.org`

---

## 📊 COMANDOS DE ADMINISTRACIÓN ÚTILES

```bash
# ============================================
# BACKEND
# ============================================

# Ver estado del backend
sudo systemctl status segar-backend

# Reiniciar backend
sudo systemctl restart segar-backend

# Detener backend
sudo systemctl stop segar-backend

# Iniciar backend
sudo systemctl start segar-backend

# Ver logs del backend (últimas 100 líneas)
sudo journalctl -u segar-backend -n 100

# Ver logs en tiempo real
sudo journalctl -u segar-backend -f

# Ver logs del archivo
tail -f ~/logs/backend.log

# ============================================
# KEYCLOAK
# ============================================

# Ver estado de Keycloak
docker ps | grep keycloak

# Ver logs de Keycloak
docker logs -f keycloak

# Reiniciar Keycloak
docker restart keycloak

# Detener Keycloak
docker stop keycloak

# Iniciar Keycloak (si está detenido)
docker start keycloak

# ============================================
# NGINX
# ============================================

# Ver estado de Nginx
sudo systemctl status nginx

# Reiniciar Nginx
sudo systemctl restart nginx

# Recargar configuración (sin interrumpir servicio)
sudo systemctl reload nginx

# Verificar configuración
sudo nginx -t

# Ver logs de Nginx
sudo tail -f /var/log/nginx/segar-backend-access.log
sudo tail -f /var/log/nginx/segar-backend-error.log

# ============================================
# CERTIFICADOS SSL
# ============================================

# Ver certificados instalados
sudo certbot certificates

# Renovar certificados manualmente
sudo certbot renew

# Renovar forzando
sudo certbot renew --force-renewal

# Ver cuando expira el certificado
echo | openssl s_client -servername segar-solutions.duckdns.org -connect segar-solutions.duckdns.org:443 2>/dev/null | openssl x509 -noout -dates

# ============================================
# SISTEMA
# ============================================

# Ver puertos en uso
sudo netstat -tulpn | grep -E ':(80|443|8080|8090)'

# Ver uso de memoria y CPU
htop

# Ver espacio en disco
df -h

# Ver logs del sistema
sudo journalctl -xe
```

---

## 🆘 TROUBLESHOOTING

### Problema: Backend no inicia

```bash
# Ver errores detallados
sudo journalctl -u segar-backend -n 200 --no-pager

# Verificar que el JAR existe
ls -lh ~/SEGAR-BackEnd/segar-backend/target/backend-*.jar

# Verificar variables de entorno
cat ~/SEGAR-BackEnd/segar-backend/.env.production

# Intentar iniciar manualmente
cd ~/SEGAR-BackEnd/segar-backend
./start-backend-production.sh
```

### Problema: Error 502 Bad Gateway

```bash
# Verificar que el backend esté corriendo
curl http://localhost:8090/actuator/health

# Ver logs de Nginx
sudo tail -n 50 /var/log/nginx/segar-backend-error.log

# Verificar que el servicio esté activo
sudo systemctl status segar-backend
```

### Problema: Keycloak no responde

```bash
# Verificar que el contenedor esté corriendo
docker ps -a | grep keycloak

# Ver logs completos
docker logs keycloak

# Reiniciar Keycloak
docker restart keycloak

# Si no existe, crearlo de nuevo
docker run -d --name keycloak --restart unless-stopped \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -e KC_PROXY=edge \
  -e KC_HOSTNAME_STRICT=false \
  -e KC_HTTP_ENABLED=true \
  -e KC_PROXY_HEADERS=xforwarded \
  quay.io/keycloak/keycloak:latest start-dev
```

### Problema: Errores de CORS en Vercel

1. Ve a Keycloak Admin Console
2. Clients → tu-cliente → Web Origins
3. Pon `*` temporalmente para probar
4. Si funciona, reemplaza por tu dominio específico

### Problema: Variables de entorno no se cargan

```bash
# Verificar que el archivo existe
ls -lh ~/SEGAR-BackEnd/segar-backend/.env.production

# Verificar permisos
chmod 600 ~/SEGAR-BackEnd/segar-backend/.env.production

# Verificar que systemd puede leerlo
sudo systemctl cat segar-backend

# Reiniciar el servicio después de cambios
sudo systemctl restart segar-backend
```

---

## ✅ CHECKLIST FINAL

### Código:
- [ ] `application-production.properties` creado
- [ ] `.env.production` creado con valores correctos
- [ ] `.gitignore` actualizado
- [ ] Código compilado (`mvnw.cmd clean package`)
- [ ] Cambios commiteados y pusheados a Git

### Servidor:
- [ ] Código actualizado (`git pull`)
- [ ] JAR compilado y verificado
- [ ] `.env.production` copiado al servidor
- [ ] Script de inicio con permisos de ejecución
- [ ] Keycloak reiniciado con configuración de proxy
- [ ] Servicio systemd configurado
- [ ] Backend corriendo (`systemctl status segar-backend`)

### Verificación:
- [ ] `curl https://segar-solutions.duckdns.org/health` responde OK
- [ ] `curl https://segar-solutions.duckdns.org/api/actuator/health` responde `{"status":"UP"}`
- [ ] Keycloak Admin Console accesible por HTTPS
- [ ] URLs de redirect configuradas en Keycloak

### Vercel:
- [ ] Variables de entorno actualizadas
- [ ] Aplicación redeployada
- [ ] Login funciona correctamente
- [ ] No hay errores de CORS
- [ ] Peticiones al backend funcionan

---

## 📚 RECURSOS ADICIONALES

- **Documentación Spring Boot**: https://spring.io/projects/spring-boot
- **Documentación Keycloak**: https://www.keycloak.org/documentation
- **Documentación Nginx**: https://nginx.org/en/docs/
- **Let's Encrypt**: https://letsencrypt.org/
- **DuckDNS**: https://www.duckdns.org/

---

## 🎯 PRÓXIMOS PASOS (Opcional)

1. **Configurar CI/CD con GitHub Actions** para automatizar despliegues
2. **Migrar de H2 a PostgreSQL** para base de datos en producción
3. **Configurar backups automáticos** de la base de datos
4. **Implementar monitoreo** con Prometheus/Grafana
5. **Configurar alertas** para caídas del servicio

---

¡Con esto tu backend está completamente configurado en producción con HTTPS! 🚀🎉
[Unit]
Description=SEGAR Backend Spring Boot Application (Production)
Documentation=https://github.com/tu-usuario/SEGAR-BackEnd
After=network.target docker.service
Wants=docker.service

[Service]
Type=simple
User=segarsolutions
Group=segarsolutions
WorkingDirectory=/home/segarsolutions/SEGAR-BackEnd/segar-backend

# Cargar variables de entorno desde archivo
EnvironmentFile=-/home/segarsolutions/SEGAR-BackEnd/segar-backend/.env.production

# Variables de entorno críticas (como respaldo)
Environment="SPRING_PROFILES_ACTIVE=production"

# Comando de ejecución - usando el script que carga las variables
ExecStart=/home/segarsolutions/SEGAR-BackEnd/scripts/start-backend-production.sh

# Política de reinicio automático
Restart=always
RestartSec=10

# Límite de intentos de reinicio
StartLimitInterval=200
StartLimitBurst=5

# Logs
StandardOutput=append:/home/segarsolutions/logs/backend.log
StandardError=append:/home/segarsolutions/logs/backend-error.log

# Configuración de seguridad
# NoNewPrivileges=true
# PrivateTmp=true

[Install]
WantedBy=multi-user.target

