package com.taskflow.repository;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        Page<Task> findByStatus(Status status, Pageable pageable);

        Page<Task> findByPriority(Priority priority, Pageable pageable);

        Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

        Page<Task> findByTitleContainingIgnoreCase(String title, Pageable pageable);

        long countByStatus(Status status);

        long countByDueDateBeforeAndStatusNot(LocalDate date, Status status);
}