package com.taskflow.controller;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.dto.UpdateTaskStatusRequest;
import com.taskflow.dto.TaskStatsDto;
import com.taskflow.dto.ErrorResponse;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasks", description = "Endpoints para gestión completa de tareas (CRUD, filtrado, paginación)")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Obtener tareas con filtrado opcional",
            description = "Devuelve lista paginada de tareas. Soporta filtrado por status, prioridad, usuario asignado o búsqueda de texto"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tareas obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @Parameter(description = "Filtrar por estado (TODO, IN_PROGRESS, DONE)")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Filtrar por prioridad (LOW, MEDIUM, HIGH, CRITICAL)")
            @RequestParam(required = false) Priority priority,
            @Parameter(description = "Filtrar por ID del usuario asignado")
            @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Buscar en título de la tarea")
            @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (0-indexed)")
            Pageable pageable
    ) {
        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Task> tasksPage = taskService.getTasks(status, priority, assigneeId, search, customPageable);

        Page<TaskResponse> responsePage = tasksPage.map(task -> new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssignee() != null ? new com.taskflow.dto.UserResponse(
                        task.getAssignee().getId(),
                        task.getAssignee().getName(),
                        task.getAssignee().getEmail(),
                        task.getAssignee().getIsActive(),
                        task.getAssignee().getCreatedAt()
                ) : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        ));

        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Obtener estadísticas del dashboard",
            description = "Devuelve contadores: tareas por estado y tareas vencidas no completadas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatsDto.class)
                    )
            )
    })
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsDto> getStats() {
        TaskStatsDto stats = taskService.getStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Obtener detalle de una tarea",
            description = "Devuelve información completa de una tarea específica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarea encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarea no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "ID de la tarea a obtener")
            @PathVariable Long id
    ) {
        Task task = taskService.getById(id);

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssignee() != null ? new com.taskflow.dto.UserResponse(
                        task.getAssignee().getId(), 
                        task.getAssignee().getName(), 
                        task.getAssignee().getEmail(), 
                        task.getAssignee().getIsActive(), 
                        task.getAssignee().getCreatedAt()
                ) : null,
                task.getCreatedAt(),
                task.getUpdatedAt()   
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Crear nueva tarea",
            description = "Registra una nueva tarea en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarea creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validación fallida"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario asignado no encontrado"
            )
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest
    ) {
        Task newTask = new Task();
        newTask.setTitle(createTaskRequest.title());
        newTask.setDescription(createTaskRequest.description());
        newTask.setStatus(createTaskRequest.status());
        newTask.setPriority(createTaskRequest.priority());
        newTask.setDueDate(createTaskRequest.dueDate());

        Task createdTask = taskService.create(newTask, createTaskRequest.assigneeId());

        TaskResponse response = new TaskResponse(
                createdTask.getId(),
                createdTask.getTitle(),
                createdTask.getDescription(),
                createdTask.getStatus(),
                createdTask.getPriority(),
                createdTask.getDueDate(),
                createdTask.getAssignee() != null ? new com.taskflow.dto.UserResponse(
                        createdTask.getAssignee().getId(),
                        createdTask.getAssignee().getName(),
                        createdTask.getAssignee().getEmail(),
                        createdTask.getAssignee().getIsActive(),
                        createdTask.getAssignee().getCreatedAt()
                ) : null,
                createdTask.getCreatedAt(),
                createdTask.getUpdatedAt()
        ); 

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar tarea completa",
            description = "Reemplaza todos los campos de una tarea existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarea actualizada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarea no encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validación fallida"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "ID de la tarea a actualizar")
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        Task updated = new Task();
        updated.setTitle(updateTaskRequest.title());
        updated.setDescription(updateTaskRequest.description());
        updated.setStatus(updateTaskRequest.status());
        updated.setPriority(updateTaskRequest.priority());
        updated.setDueDate(updateTaskRequest.dueDate());

        Task updateTask = taskService.update(id, updated, updateTaskRequest.assigneeId());

        TaskResponse response = new TaskResponse(
                updateTask.getId(),
                updateTask.getTitle(),
                updateTask.getDescription(),
                updateTask.getStatus(),
                updateTask.getPriority(),
                updateTask.getDueDate(),
                updateTask.getAssignee() != null ? new com.taskflow.dto.UserResponse(
                    updateTask.getAssignee().getId(), 
                    updateTask.getAssignee().getName(), 
                    updateTask.getAssignee().getEmail(), 
                    updateTask.getAssignee().getIsActive(), 
                    updateTask.getAssignee().getCreatedAt()
                ) : null,
                updateTask.getCreatedAt(),
                updateTask.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar solo el estado de una tarea",
            description = "Modifica únicamente el status (TODO, IN_PROGRESS, DONE) de una tarea"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarea no encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Status inválido"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @Parameter(description = "ID de la tarea a actualizar")
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest
    ) {
        Status newStatus = updateTaskStatusRequest.status();
        Task updateTask = taskService.updateStatus(id, newStatus);  

        TaskResponse response = new TaskResponse(
            updateTask.getId(),
            updateTask.getTitle(),
            updateTask.getDescription(),
            updateTask.getStatus(),
            updateTask.getPriority(),
            updateTask.getDueDate(),
            updateTask.getAssignee() != null ? new com.taskflow.dto.UserResponse(
                updateTask.getAssignee().getId(),
                updateTask.getAssignee().getName(),
                updateTask.getAssignee().getEmail(),
                updateTask.getAssignee().getIsActive(),
                updateTask.getAssignee().getCreatedAt()
            ) : null,
            updateTask.getCreatedAt(),
            updateTask.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar una tarea",
            description = "Borra permanentemente una tarea del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarea eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarea no encontrada"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID de la tarea a eliminar")
            @PathVariable Long id
    ) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
