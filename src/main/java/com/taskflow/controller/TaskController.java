package com.taskflow.controller;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.dto.UpdateTaskStatusRequest;
import com.taskflow.dto.TaskStatsDto;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
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

    @GetMapping("/stats")
    public ResponseEntity<TaskStatsDto> getStats() {
        TaskStatsDto stats = taskService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
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

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask (
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
