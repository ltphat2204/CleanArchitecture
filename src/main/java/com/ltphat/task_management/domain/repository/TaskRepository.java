package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
