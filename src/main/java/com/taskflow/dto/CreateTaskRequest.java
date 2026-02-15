package com.taskflow.dto;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
    @NotBlank(message = "El titulo es obligatorio")
    @Size(min = 3, max = 200, message = "El titulo debe tener entre 3 y 200 caracteres")
    String title,
    
    String description,
    
    @NotNull(message = "El estado es obligatorio")
    Status status,
    
    @NotNull(message = "La prioridad es obligatoria")
    Priority priority,
    
    LocalDate dueDate,
    
    Long assigneeId
) {}
