# Backend EYK Corp – Microservicio de Clientes

Microservicio REST para la gestión de clientes (CRUD), desarrollado como parte de la prueba técnica para Desarrollador Fullstack de EYK Corp.

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje |
| Spring Boot | 3.5.6 | Framework (Web, Data JPA, Validation) |
| PostgreSQL | 16 | Base de datos |
| Hibernate / JPA | 6.x | ORM |
| Lombok | — | Reducción de código repetitivo |
| Maven | 3.9 (wrapper incluido) | Gestión de dependencias y build |
| Docker / Docker Compose | — | Contenedores y orquestación |

---

## Ejecución rápida (Docker Compose — aplicación completa)

Este `docker-compose.yml` levanta **toda la aplicación**: PostgreSQL, backend y el frontend en Vue servido por Nginx. El frontend vive en otro repositorio ([frontend_eykcorp](https://github.com/MichaelPaucar07/frontend_eykcorp)), por lo que ambos deben clonarse con esta estructura de carpetas:

```
Repositories/
├── Springboot/backend_eykcorp/   ← este repositorio (aquí está el docker-compose.yml)
└── Vue/frontend_eykcorp/
```

> Si el frontend está en otra ruta, se indica con la variable `FRONTEND_PATH` en el `.env` (relativa a esta carpeta).

**Requisito:** tener Docker Desktop instalado y en ejecución.

```bash
mkdir -p Repositories/Springboot Repositories/Vue
git clone https://github.com/MichaelPaucar07/backend_eykcorp.git Repositories/Springboot/backend_eykcorp
git clone https://github.com/MichaelPaucar07/frontend_eykcorp.git Repositories/Vue/frontend_eykcorp

cd Repositories/Springboot/backend_eykcorp
cp .env.example .env
docker compose up -d --build
```

Esto levanta tres contenedores:

| Contenedor | Puerto en el host | Descripción |
|---|---|---|
| `eykcorp_frontend` | `3000` | SPA en Vue + Nginx (reverse proxy `/api` → backend) |
| `eykcorp_backend` | `8081` | API REST |
| `eykcorp_postgres` | `5433` | PostgreSQL (puerto interno 5432) |

- Aplicación web: **http://localhost:3000**
- API directa: **http://localhost:8081/clientes** (o a través de Nginx: http://localhost:3000/api/clientes)

```
Navegador ──▶ localhost:3000 ──▶ [Nginx: eykcorp_frontend]
                                   ├── /        → archivos estáticos de Vue (dist/)
                                   └── /api/... → backend:8081 ──▶ postgres:5432
```

Como el navegador solo habla con Nginx (mismo origen), en Docker **no se necesita CORS**; CORS solo aplica en desarrollo, cuando Vite corre en `localhost:5173`.

Para verificar que todo esté funcionando:

```bash
docker ps
curl http://localhost:3000/api/clientes
```

Comandos útiles:

```bash
docker compose logs -f backend   # ver logs del backend en vivo
docker compose up -d postgres backend   # levantar solo base de datos y API
docker compose down              # detener (los datos se conservan)
docker compose down -v           # detener y borrar los datos
```

> El backend espera a que PostgreSQL esté **healthy** (`depends_on` + `healthcheck`) antes de iniciar.

## Ejecución en modo desarrollo (sin Docker para el backend)

**Requisitos:** Java 17 y Docker (solo para la base de datos).

```bash
# 1. Levantar solo PostgreSQL
docker compose up -d postgres

# 2. Ejecutar la aplicación con el wrapper de Maven
./mvnw spring-boot:run
```

En Windows (PowerShell/CMD) usar `mvnw.cmd spring-boot:run`.

---

## Variables de entorno

Todas las propiedades sensibles o dependientes del entorno se leen desde variables de entorno, con valores por defecto para desarrollo local (`application.properties`). La plantilla está en [`.env.example`](.env.example).

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `SERVER_PORT` | `8081` | Puerto HTTP de la API |
| `DB_URL` | `jdbc:postgresql://localhost:5433/db_eykcorp` | URL JDBC (en Docker se sobrescribe a `postgres:5432`) |
| `DB_NAME` | `db_eykcorp` | Nombre de la base de datos (usado por Docker Compose) |
| `DB_PORT` | `5433` | Puerto de PostgreSQL expuesto en el host |
| `DB_USERNAME` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | `root` | Contraseña (**solo para desarrollo local**) |
| `JPA_DDL_AUTO` | `update` | Estrategia de Hibernate para el esquema |
| `JPA_SHOW_SQL` | `true` (local) / `false` (Docker) | Mostrar SQL en consola |
| `APP_LOG_LEVEL` | `DEBUG` (local) / `INFO` (Docker) | Nivel de log del paquete de la aplicación |
| `FRONTEND_PATH` | `../../Vue/frontend_eykcorp` | Ruta del repositorio del frontend (build del servicio `frontend`) |
| `FRONTEND_PORT` | `3000` | Puerto del frontend (Nginx) en el host |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes permitidos para el frontend (separados por coma) |

> El archivo `.env` está excluido de Git (`.gitignore`) y de la imagen Docker (`.dockerignore`).

---

## Estructura del proyecto

Arquitectura **por capas**:

```
src/main/java/com/michaeldev/backend_eykcorp/
├── BackendEykcorpApplication.java     # Punto de entrada
├── config/
│   └── CorsConfig.java                # Configuración de CORS
├── controller/
│   └── ClienteController.java         # Endpoints REST (capa HTTP)
├── service/
│   ├── ClienteService.java            # Contrato de la lógica de negocio
│   └── impl/ClienteServiceImpl.java   # Reglas de negocio, transacciones y logs
├── repository/
│   └── ClienteRepository.java         # Acceso a datos (Spring Data JPA)
├── entity/
│   └── Cliente.java                   # Entidad JPA → tabla "clientes"
├── dto/
│   ├── ClienteRequestDTO.java         # Entrada: validaciones y normalización
│   ├── ClienteResponseDTO.java        # Salida
│   ├── ApiResponse.java               # Estructura única de respuesta
│   └── PageResponse.java              # Estructura de respuestas paginadas
├── mapper/
│   ├── ClienteMapper.java             # Contrato de conversión Entity ↔ DTO
│   └── impl/ClienteMapperImpl.java
└── exception/
    ├── BusinessException.java         # Base de excepciones de negocio (con HttpStatus)
    ├── ResourceNotFoundException.java # 404
    ├── DuplicateResourceException.java# 409
    └── GlobalExceptionHandler.java    # Manejo global de excepciones
```

Flujo de una petición:

```
Cliente HTTP → Controller → Service → Repository → PostgreSQL
                  ↑            ↓
                 DTO  ←──  Mapper  ←── Entity

Cualquier excepción → GlobalExceptionHandler → ApiResponse de error
```

Archivos de infraestructura:

| Archivo | Descripción |
|---|---|
| `Dockerfile` | Build multi-stage (Maven + JDK → JRE Alpine), usuario sin privilegios |
| `.dockerignore` | Excluye `target/`, `.git/`, `.env`, etc. del contexto de build |
| `docker-compose.yml` | Servicios `postgres`, `backend` y `frontend`, red interna, volumen y healthcheck |
| `.env.example` | Plantilla de variables de entorno |

---

## Modelo de datos

Tabla `clientes`:

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | `BIGINT` | PK, autogenerado (`IDENTITY`) |
| `nombres` | `VARCHAR(100)` | `NOT NULL` |
| `apellidos` | `VARCHAR(100)` | `NOT NULL` |
| `correo` | `VARCHAR(150)` | `NOT NULL`, `UNIQUE` |
| `telefono` | `VARCHAR(15)` | `NOT NULL` |
| `fecha_creacion` | `TIMESTAMP` | `NOT NULL`, no actualizable |

---

## Endpoints

URL base: `http://localhost:8081`

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| `POST` | `/clientes` | Crear cliente | `201 Created` + header `Location` |
| `GET` | `/clientes?page=0&size=10` | Listar clientes (paginado) | `200 OK` |
| `GET` | `/clientes/{id}` | Obtener cliente por id | `200 OK` |
| `PUT` | `/clientes/{id}` | Actualizar cliente | `200 OK` |
| `DELETE` | `/clientes/{id}` | Eliminar cliente | `200 OK` |

### Cuerpo de la petición (POST / PUT)

```json
{
  "nombres": "Michael",
  "apellidos": "Paucar",
  "correo": "michael@test.com",
  "telefono": "+593991234567"
}
```

`id` y `fechaCreacion` **no** se envían: los asigna el servidor.

### Paginación

| Parámetro | Por defecto | Validación |
|---|---|---|
| `page` | `0` | `>= 0` |
| `size` | `10` | entre `1` y `100` |

Los resultados se ordenan por `id` ascendente.

---

## Estructura de respuesta

**Todas** las respuestas (éxito y error) tienen la misma estructura:

```json
{
  "success": true,
  "status": 201,
  "message": "Cliente creado correctamente",
  "data": {
    "id": 1,
    "nombres": "Michael",
    "apellidos": "Paucar",
    "correo": "michael@test.com",
    "telefono": "+593991234567",
    "fechaCreacion": "2026-10-08T12:31:17.563"
  },
  "errors": null,
  "timestamp": "2026-10-08T12:31:17.593"
}
```

Listado paginado (`data` contiene un `PageResponse`):

```json
{
  "success": true,
  "status": 200,
  "message": "Clientes obtenidos correctamente",
  "data": {
    "content": [ { "id": 1, "nombres": "Michael", "...": "..." } ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "errors": null,
  "timestamp": "2026-10-08T12:40:45.110"
}
```

Error de validación (`errors` contiene el detalle por campo):

```json
{
  "success": false,
  "status": 400,
  "message": "La solicitud contiene datos inválidos",
  "data": null,
  "errors": {
    "nombres": "Los nombres son obligatorios",
    "correo": "El correo no tiene un formato válido",
    "telefono": "El teléfono debe tener formato internacional con código de país, ej. +593991234567"
  },
  "timestamp": "2026-10-08T12:31:17.784"
}
```

### Códigos de error

| Código | Cuándo ocurre |
|---|---|
| `400` | Validaciones del cuerpo, JSON mal formado, parámetros inválidos (`/clientes/abc`, `page=-1`) |
| `404` | Cliente no encontrado o ruta inexistente |
| `405` | Método HTTP no soportado |
| `409` | Correo ya registrado (en otro cliente) |
| `500` | Error interno inesperado (el detalle solo se registra en el log, nunca se expone) |

---

## Reglas de negocio

1. `nombres` y `apellidos` son obligatorios, de 2 a 100 caracteres y **solo letras** (se admiten tildes, `ñ`, y un espacio, apóstrofo o guion entre palabras: `José María`, `Peña-O'Neil`).
2. `correo` es obligatorio, con formato `usuario@dominio.ext` (exige extensión de dominio) y máx. 150 caracteres.
3. `correo` es **único**: validado en el servicio (409) y respaldado por una restricción `UNIQUE` en la base de datos para peticiones concurrentes.
4. Al **actualizar**, un cliente puede conservar su propio correo, pero no usar el de otro cliente.
5. `telefono` es obligatorio y se guarda en **formato internacional E.164**: `+<código de país><número>`, por ejemplo `+593991234567`. El frontend pide el número nacional (Ecuador: 10 dígitos, `0991234567`) junto con el código de país y lo convierte.
6. `fecha_creacion` la asigna el servidor (`@PrePersist`) y no puede modificarse (`updatable = false`).
7. `id` es autogenerado por la base de datos.
8. Operaciones sobre un `id` inexistente responden `404`.
9. Los datos de entrada se **normalizan** antes de validarse: se eliminan espacios al inicio/fin, se deja un solo espacio entre palabras, el correo se guarda en minúsculas (`Ana@Mail.com` y `ana@mail.com` se consideran el mismo correo) y del teléfono se quitan espacios, guiones y paréntesis (`+593 99 123 4567` → `+593991234567`).

---

## Consideraciones técnicas

- **Arquitectura por capas** con separación estricta: el controller solo maneja HTTP, el service contiene las reglas de negocio y la entidad nunca se expone fuera del service (se usan DTOs).
- **Principios SOLID**:
  - *S*: cada clase tiene una única responsabilidad (validación/normalización en el DTO, conversión en el mapper, reglas en el service, HTTP en el controller).
  - *O*: `BusinessException` define el código HTTP en cada subclase; agregar una nueva excepción de negocio no requiere modificar el `GlobalExceptionHandler`.
  - *L*: las subclases de `BusinessException` y las implementaciones de las interfaces son intercambiables.
  - *I*: interfaces pequeñas y cohesivas (`ClienteService`, `ClienteMapper`).
  - *D*: el controller y el service dependen de interfaces, con inyección por constructor.
- **Estructura única de respuesta** (`ApiResponse<T>`) para simplificar el consumo desde el frontend.
- **`DELETE` responde `200`** con `ApiResponse` en lugar de `204 No Content`, ya que un `204` no puede llevar cuerpo y rompería la estructura única.
- **Paginación** con un DTO propio (`PageResponse`) en lugar de serializar `Page` de Spring, cuya estructura JSON no es estable. Tamaño máximo de página limitado a 100.
- **Transacciones**: `@Transactional` en operaciones de escritura y `readOnly = true` en lecturas.
- **Logs**: `INFO` en operaciones de escritura, `DEBUG` en lecturas, `WARN` en errores de negocio/validación y `ERROR` con stacktrace en errores inesperados.
- **CORS** configurable por variable de entorno; solo permite los métodos usados por la API y expone el header `Location`. En Docker no se usa, porque Nginx sirve el frontend y la API bajo el mismo origen.
- **Reverse proxy**: con `server.forward-headers-strategy=framework`, el backend respeta los headers `X-Forwarded-*` de Nginx (el header `Location` de un `201` apunta a `http://localhost:3000/api/clientes/{id}`).
- **Docker**:
  - Imagen multi-stage: la imagen final solo contiene el JRE y el `.jar`.
  - La aplicación se ejecuta con un usuario sin privilegios (`spring`).
  - Zona horaria del contenedor: `America/Guayaquil`.
  - PostgreSQL se expone en el puerto `5433` del host para no entrar en conflicto con una instalación local en el `5432`. Dentro de la red de Docker, el backend se conecta a `postgres:5432`.
- **`ddl-auto=update`** se usa por simplicidad en el contexto de la prueba. En producción se recomienda una herramienta de migraciones (Flyway o Liquibase) y `ddl-auto=validate`.

---

## Pruebas rápidas con curl

```bash
# Crear
curl -i -X POST http://localhost:8081/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombres":"Michael","apellidos":"Paucar","correo":"michael@test.com","telefono":"+593991234567"}'

# Listar (paginado)
curl -i "http://localhost:8081/clientes?page=0&size=10"

# Obtener por id
curl -i http://localhost:8081/clientes/1

# Actualizar
curl -i -X PUT http://localhost:8081/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombres":"Michael A.","apellidos":"Paucar","correo":"michael@test.com","telefono":"+593991234567"}'

# Eliminar
curl -i -X DELETE http://localhost:8081/clientes/1
```

---

## Autor

Michael Paucar – [GitHub](https://github.com/MichaelPaucar07)
