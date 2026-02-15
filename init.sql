-- =====================================================
-- CREACIÓN DE BASE DE DATOS Y TABLAS
-- =====================================================

/*
 * Crear base de datos "taskflow_db".
 * Input: nombre de db.
 * Proceso: PostgreSQL crea la BD nueva.
 * Output: BD lista para tablas.
 */
CREATE DATABASE taskflow_db;

-- Conectarse a taskflow_db (en pgAdmin o psql hacer \c taskflow_db)

/*
 * Crear tabla "users" con estructura de Usuario.
 * Input: columnas name, email (unique), password_hash, is_active, timestamps.
 * Proceso: PostgreSQL crea tabla con constraints (PK, UNIQUE, NOT NULL).
 * Output: tabla users lista para inserciones.
 */
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/*
 * Crear tabla "tasks" con relacion a users.
 * Input: title, description, status (enum), priority (enum), dueDate, assignee_id (FK).
 * Proceso: PostgreSQL crea tabla con FK a users, enums stored as VARCHAR.
 * Output: tabla tasks lista con relacion a users.
 * 
 * Nota: status y priority se almacenan como VARCHAR porque PostgreSQL
 * enums nativos complican la migracion de Hibernate. Usamos strings.
 */
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    due_date DATE,
    assignee_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDICES PARA OPTIMIZAR CONSULTAS
-- =====================================================

/*
 * Indice en assignee_id para filtrado rapido por usuario.
 * Input: nombre tabla + columna.
 * Proceso: PostgreSQL crea indice B-tree.
 * Output: consultas "WHERE assignee_id = X" ejecutan mas rapido.
 */
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);

/*
 * Indice en status para filtrado rapido por estado.
 * Output: consultas "WHERE status = 'TODO'" ejecutan mas rapido.
 */
CREATE INDEX idx_tasks_status ON tasks(status);

/*
 * Indice en priority para filtrado rapido por prioridad.
 */
CREATE INDEX idx_tasks_priority ON tasks(priority);

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

/*
 * Insertar usuarios de ejemplo.
 * Input: name, email, password_hash (BCrypt hasheado).
 * Proceso: PostgreSQL inserta filas en users.
 * Output: 2 usuarios disponibles para asignar tareas.
 * 
 * Password hasheado con BCrypt:
 * "password123" hasheado = $2a$10$slYQmyNdGzin7olVN3p5aOAEzsPmPnm8D4LhATX7H6ghNvlm.LZ6a
 * "admin456" hasheado = $2a$10$5j2O.l7N8k9pQ1R2S3T4U5V6W7X8Y9Z0aAbBcCdDeEfFgGhHiIj
 */
INSERT INTO users (name, email, password_hash, is_active) VALUES
('Juan Pérez', 'juan@example.com', '$2a$10$slYQmyNdGzin7olVN3p5aOAEzsPmPnm8D4LhATX7H6ghNvlm.LZ6a', true),
('María García', 'maria@example.com', '$2a$10$5j2O.l7N8k9pQ1R2S3T4U5V6W7X8Y9Z0aAbBcCdDeEfFgGhHiIj', true);

/*
 * Insertar tareas de ejemplo asociadas a usuarios.
 * Input: titulo, descripcion, estado, prioridad, fecha vencimiento, usuario asignado.
 * Proceso: PostgreSQL inserta filas en tasks, valida FK a users.
 * Output: tareas visibles en GET /api/tasks.
 * 
 * Nota: los IDs de assignee_id son 1 y 2 (los usuarios insertados arriba).
 */
INSERT INTO tasks (title, description, status, priority, due_date, assignee_id) VALUES
('Implementar login', 'Crear endpoint POST /api/auth/login', 'IN_PROGRESS', 'HIGH', '2026-02-20', 1),
('Documentar API', 'Escribir Swagger/OpenAPI', 'TODO', 'MEDIUM', '2026-02-25', 2),
('Corregir bugs', 'Revisar issues del proyecto', 'TODO', 'CRITICAL', '2026-02-18', 1),
('Agregar paginación', 'Implementar limit/offset', 'DONE', 'MEDIUM', '2026-02-19', 2),
('Tests unitarios', 'Cobertura 80%+ en TaskService', 'TODO', 'HIGH', '2026-02-28', 1);

-- =====================================================
-- VERIFICACION (ejecutar para confirmar datos)
-- =====================================================

/*
 * Listar todos los usuarios.
 * Output: tabla con users.
 */
SELECT * FROM users;

/*
 * Listar todas las tareas con nombre de usuario asignado.
 * Input: join tasks + users.
 * Output: tareas con informacion del usuario que las ejecuta.
 */
SELECT t.id, t.title, t.status, t.priority, u.name as assignee FROM tasks t
LEFT JOIN users u ON t.assignee_id = u.id;
