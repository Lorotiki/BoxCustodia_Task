package com.taskflow.dto;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    Status status,
    Priority priority,
    LocalDate dueDate,
    UserResponse assignee,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            task.getAssignee() != null ? UserResponse.from(task.getAssignee()) : null,
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
