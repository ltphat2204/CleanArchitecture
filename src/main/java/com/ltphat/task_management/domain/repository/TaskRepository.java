package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
