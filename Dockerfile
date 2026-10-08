# ---------- ETAPA 1: COMPILACIÓN ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# DESCARGA DE DEPENDENCIAS (CAPA CACHEADA MIENTRAS NO CAMBIE EL POM)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# COMPILACIÓN DEL CÓDIGO FUENTE
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- ETAPA 2: EJECUCIÓN ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# USUARIO SIN PRIVILEGIOS PARA EJECUTAR LA APLICACIÓN
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
