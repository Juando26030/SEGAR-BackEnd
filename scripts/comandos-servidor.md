# Comandos Rápidos para el Servidor

## 📋 Comandos que debes ejecutar en orden en tu servidor Linux

### 1️⃣ Instalar Nginx

```bash
sudo apt update
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

### 2️⃣ Instalar Certbot

```bash
sudo apt install certbot python3-certbot-nginx -y
```

### 3️⃣ Copiar archivo de configuración de Nginx

Primero, sube el archivo `nginx-config/segar-backend.conf` a tu servidor:

```bash
# En tu máquina local (Windows), usa SCP o SFTP para subir el archivo
# O copia manualmente el contenido

# En el servidor Linux:
sudo nano /etc/nginx/sites-available/segar-backend
# Pega el contenido del archivo segar-backend.conf
# ⚠️ IMPORTANTE: Reemplaza "api.tudominio.com" con tu dominio real
# Guarda con Ctrl+X, luego Y, luego Enter
```

### 4️⃣ Activar la configuración

```bash
sudo ln -s /etc/nginx/sites-available/segar-backend /etc/nginx/sites-enabled/
sudo rm /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

### 5️⃣ Obtener certificado SSL

```bash
# ⚠️ Asegúrate de que tu dominio apunte a la IP del servidor antes de ejecutar esto
sudo certbot --nginx -d api.tudominio.com
# Sigue las instrucciones en pantalla
```

### 6️⃣ Preparar scripts del backend

```bash
# Crear directorio para logs
mkdir -p ~/logs

# Crear el script de inicio
nano ~/SEGAR-BackEnd/segar-backend/start-backend.sh
# Pega el contenido del archivo scripts/start-backend.sh
# ⚠️ IMPORTANTE: Reemplaza "api.tudominio.com" con tu dominio real
# Guarda con Ctrl+X, luego Y, luego Enter

# Dar permisos de ejecución
chmod +x ~/SEGAR-BackEnd/segar-backend/start-backend.sh
```

### 7️⃣ Reiniciar Keycloak con configuración de proxy

```bash
# Crear directorio para datos de Keycloak (persistencia)
mkdir -p ~/keycloak-config/data

# Crear el script
nano ~/keycloak-config/start-keycloak-docker.sh
# Pega el contenido del archivo scripts/start-keycloak-docker.sh
# Guarda con Ctrl+X, luego Y, luego Enter

# Dar permisos de ejecución
chmod +x ~/keycloak-config/start-keycloak-docker.sh

# Ejecutar el script
~/keycloak-config/start-keycloak-docker.sh
```

### 8️⃣ Compilar el backend (si no está compilado)

```bash
cd ~/SEGAR-BackEnd/segar-backend
./mvnw clean package -DskipTests
```

### 9️⃣ Iniciar el backend

```bash
# Opción A: Ejecutar en primer plano (para ver logs)
~/SEGAR-BackEnd/segar-backend/start-backend.sh

# Opción B: Ejecutar en segundo plano
nohup ~/SEGAR-BackEnd/segar-backend/start-backend.sh > ~/logs/backend.log 2>&1 &

# Ver logs en tiempo real
tail -f ~/logs/backend.log
```

### 🔟 Configurar como servicio systemd (RECOMENDADO)

```bash
# Crear el archivo de servicio
sudo nano /etc/systemd/system/segar-backend.service
# Pega el contenido del archivo scripts/segar-backend.service
# ⚠️ IMPORTANTE: Reemplaza "api.tudominio.com" con tu dominio real
# Guarda con Ctrl+X, luego Y, luego Enter

# Recargar systemd
sudo systemctl daemon-reload

# Habilitar el servicio para que inicie automáticamente
sudo systemctl enable segar-backend

# Iniciar el servicio
sudo systemctl start segar-backend

# Ver estado
sudo systemctl status segar-backend

# Ver logs
sudo journalctl -u segar-backend -f
```

---

## 🔍 Verificar que todo funciona

```bash
# 1. Verificar Nginx
curl -I https://api.tudominio.com/health

# 2. Verificar Backend
curl https://api.tudominio.com/api/actuator/health

# 3. Verificar Keycloak
curl https://api.tudominio.com/auth/realms/segar

# 4. Ver logs de Nginx
sudo tail -f /var/log/nginx/segar-backend-error.log

# 5. Ver logs del Backend
sudo journalctl -u segar-backend -f
# o si no usas systemd:
tail -f ~/logs/backend.log

# 6. Ver logs de Keycloak
docker logs -f keycloak
```

---

## 🛠️ Comandos útiles de administración

### Reiniciar servicios

```bash
# Reiniciar Nginx
sudo systemctl restart nginx

# Reiniciar Backend
sudo systemctl restart segar-backend

# Reiniciar Keycloak
docker restart keycloak
```

### Ver estado de servicios

```bash
# Estado de Nginx
sudo systemctl status nginx

# Estado del Backend
sudo systemctl status segar-backend

# Estado de Keycloak
docker ps | grep keycloak
```

### Detener servicios

```bash
# Detener Backend
sudo systemctl stop segar-backend

# Detener Keycloak
docker stop keycloak
```

### Ver puertos en uso

```bash
sudo netstat -tulpn | grep -E ':(80|443|8080|8090)'
```

---

## 🔒 Configurar Firewall de Google Cloud

En **Google Cloud Console**:

1. Ve a **VPC Network** > **Firewall**
2. Crea/verifica estas reglas:

**Regla 1: Permitir HTTP**
- Nombre: `allow-http`
- Destinos: Todas las instancias de la red
- Filtro de origen: `0.0.0.0/0`
- Protocolos y puertos: `tcp:80`

**Regla 2: Permitir HTTPS**
- Nombre: `allow-https`
- Destinos: Todas las instancias de la red
- Filtro de origen: `0.0.0.0/0`
- Protocolos y puertos: `tcp:443`

---

## 📝 Checklist de ejecución

- [ ] Nginx instalado y corriendo
- [ ] Certbot instalado
- [ ] Configuración de Nginx creada en `/etc/nginx/sites-available/segar-backend`
- [ ] Dominio apuntando a la IP del servidor
- [ ] Certificado SSL obtenido con Let's Encrypt
- [ ] Script `start-backend.sh` creado y con permisos de ejecución
- [ ] Script `start-keycloak-docker.sh` creado y ejecutado
- [ ] Backend compilado (archivo JAR existe)
- [ ] Servicio systemd configurado (opcional)
- [ ] Backend iniciado y respondiendo
- [ ] Keycloak iniciado y respondiendo
- [ ] Firewall de Google Cloud configurado
- [ ] Pruebas con curl exitosas
- [ ] Variables de entorno actualizadas en Vercel

---

## ⚠️ Notas importantes

1. **Reemplaza `api.tudominio.com`** con tu dominio real en TODOS los archivos
2. **Asegúrate de que el dominio apunte a tu IP** antes de ejecutar Certbot
3. **Los puertos 8080 y 8090 solo deben ser accesibles desde localhost** (no desde internet)
4. **Actualiza las URLs en Keycloak Admin Console** después de configurar HTTPS
5. **Actualiza las variables de entorno en Vercel** con las nuevas URLs HTTPS

