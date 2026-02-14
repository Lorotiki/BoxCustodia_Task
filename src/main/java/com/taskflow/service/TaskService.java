package com.taskflow.service;

import com.taskflow.dto.TaskStatsDto;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<Task> getTasks(Status status, Priority priority, Long assigneeId, String search, Pageable pageable) {
        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }
    
        if (priority != null) {
            return taskRepository.findByPriority(priority, pageable);
        }
        if (assigneeId != null) {
            return taskRepository.findByAssigneeId(assigneeId, pageable);
        }
        if (search != null && !search.isBlank()) {
            return taskRepository.findByTitleContainingIgnoreCase(search, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    public Task getById(Long id ) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada: " + id));
    }

    public Task create(Task task, Long assigneeId) {
        task.setAssignee(resolveAssignee(assigneeId));
        return taskRepository.save(task);
    }

    public Task update(Long id, Task updated, Long assigneeId) {
        Task existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());
        existing.setDueDate(updated.getDueDate());
        existing.setAssignee(resolveAssignee(assigneeId));
        return taskRepository.save(existing);
    }

        public Task updateStatus(Long id, Status status) {
        Task existing = getById(id);
        existing.setStatus(status);
        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        Task existing = getById(id);
        taskRepository.delete(existing);
    }

    public TaskStatsDto getStats() {
        long total = taskRepository.count();
        long inProgress = taskRepository.countByStatus(Status.IN_PROGRESS);
        long completed = taskRepository.countByStatus(Status.DONE);
        long overdue = taskRepository.countByDueDateBeforeAndStatusNot(LocalDate.now(), Status.DONE);
        return new TaskStatsDto(total, inProgress, completed, overdue);
    }

    private User resolveAssignee(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + assigneeId));
    }
}