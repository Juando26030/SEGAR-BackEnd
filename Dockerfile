# Etapa de compilación
FROM amazoncorretto:21-alpine3.19 AS builder
WORKDIR /app

# Copiar archivos de construcción
COPY segar-backend/pom.xml .
COPY segar-backend/mvnw .
COPY segar-backend/.mvn .mvn

# Descargar dependencias para cachear
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY segar-backend/src src

# Compilar la aplicación (sin tests)
RUN ./mvnw clean package -DskipTests

# Etapa final (runtime)
FROM amazoncorretto:21-alpine3.19
WORKDIR /app

# Copiar el JAR compilado desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Copiar las credenciales si están en resources
COPY segar-backend/src/main/resources/gcp-service-account.json /app/gcp-service-account.json

# Exponer el puerto de Spring Boot
EXPOSE 8090

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
