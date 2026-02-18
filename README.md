# TaskFlow - API REST de Gestión de Tareas

API REST desarrollada con Spring Boot 4.0.2 para la gestión de tareas con autenticación de usuario, filtrado avanzado y estadísticas de tareas.

## Descripción del Proyecto

TaskFlow es una aplicación backend que permite:
- **Autenticación**: Login de usuarios con contraseña hasheada (BCrypt)
- **Gestión de Tareas**: Crear, leer, actualizar y eliminar tareas
- **Filtrado Avanzado**: Filtrar tareas por estado, prioridad, usuario asignado y búsqueda por título
- **Paginación**: Soporte completo para paginación de resultados
- **Estadísticas**: Endpoint de estadísticas para contar tareas por estado y prioridad
- **Documentación Interactiva**: Swagger UI para explorar y probar la API

## Requisitos Previos

Asegúrate de tener instalados:
- **Java 17** o superior
- **Maven 3.8.1** o superior
- **PostgreSQL 12** o superior

## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Lorotiki/BoxCustodia_Task.git
cd BoxCustodia_Auth
```

### 2. Configurar la Base de Datos PostgreSQL

#### Opción A: Ejecutar el Script SQL

```bash
# Conectarse a PostgreSQL como usuario postgres
sudo -u postgres psql

# En la consola de psql, ejecutar:
\i path/to/init.sql
```

#### Opción B: Usar el archivo init.sql desde la terminal

```bash
cat init.sql | sudo -u postgres psql
```

El script ejecutará:
- Creación de la base de datos `taskflow_db`
- Creación de las tablas `users` y `tasks`
- Creación de índices para optimización
- Inserción de datos de prueba (2 usuarios y 5 tareas)

**Datos de prueba incluidos:**
- **Usuario 1**: Email `juan.perez@example.com`, Contraseña: `password123`
- **Usuario 2**: Email `maria.garcia@example.com`, Contraseña: `password456`

### 3. Compilar el Proyecto

```bash
mvn clean compile
```

## Ejecución

### Iniciar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080`

### Acceder a la Documentación de la API

Una vez que la aplicación esté corriendo, accede a:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Especificación OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Endpoints Disponibles

### Autenticación
- `POST /api/auth/login` - Login de usuario

### Usuarios
- `GET /api/users` - Obtener lista de todos los usuarios

### Tareas
- `GET /api/tasks` - Obtener tareas con filtros opcionales
  - Parámetros: `status`, `priority`, `assigneeId`, `search`, `page`, `size`, `sort`
- `GET /api/tasks/stats` - Obtener estadísticas de tareas
- `GET /api/tasks/{id}` - Obtener una tarea por ID
- `POST /api/tasks` - Crear una nueva tarea (HTTP 201)
- `PUT /api/tasks/{id}` - Actualizar una tarea completamente
- `PATCH /api/tasks/{id}/status` - Actualizar solo el estado de una tarea
- `DELETE /api/tasks/{id}` - Eliminar una tarea (HTTP 204)

## Estructura del Proyecto

```
src/main/java/com/taskflow/
├── controller/          # Controllers REST
├── service/             # Lógica de negocio
├── repository/          # Acceso a datos (JPA)
├── model/               # Entidades JPA
├── dto/                 # Objetos de transferencia
├── exception/           # Excepciones personalizadas
├── config/              # Configuración (Swagger, Security)
└── TaskflowApplication  # Clase principal
```

## Configuración Adicional

### application.properties

El archivo `application.properties` contiene:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

**Nota**: Ajusta las credenciales de PostgreSQL según tu configuración local si es necesario.

## Ejemplo de Uso

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "password123"
  }'
```

### 2. Crear una Tarea
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mi primera tarea",
    "description": "Descripción de la tarea",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2025-03-15",
    "assigneeId": 1
  }'
```

### 3. Obtener Tareas con Filtros
```bash
curl http://localhost:8080/api/tasks?status=IN_PROGRESS&priority=HIGH&page=0&size=10
```

## Solución de Problemas

### Error: "FATAL: Ident authentication failed for user 'postgres'"

Ejecuta el comando con `sudo`:
```bash
cat init.sql | sudo -u postgres psql
```

### Error: "Cannot find database taskflow_db"

Verifica que el script init.sql se ejecutó correctamente y que PostgreSQL está corriendo.

### Error de Conexión a PostgreSQL

Asegúrate que:
1. PostgreSQL está corriendo: `sudo service postgresql status`
2. Las credenciales en `application.properties` son correctas

## Dependencias Principales

- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **Spring Web**
- **PostgreSQL Driver**
- **Lombok**
- **Spring Security Crypto** (BCrypt)
- **Springdoc OpenAPI** (Swagger)
- **Validation (JSR-380)**

## Notas de Desarrollo

- Las contraseñas se hashean automáticamente con BCrypt
- Las fechas se manejan en formato ISO (YYYY-MM-DD)
- La paginación usa el formato de Spring Data (starts at page 0)
- Los emails deben ser únicos en el sistema
- Las tareas pueden tener un usuario asignado opcional

## Autor

Desarrollado como solución al Desafío Técnico de TaskFlow.

## Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.
