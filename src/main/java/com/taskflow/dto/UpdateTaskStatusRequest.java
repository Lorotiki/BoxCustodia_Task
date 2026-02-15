package com.taskflow.dto;

import com.taskflow.model.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
    @NotNull(message = "El estado es obligatorio")
    Status status
) {}
