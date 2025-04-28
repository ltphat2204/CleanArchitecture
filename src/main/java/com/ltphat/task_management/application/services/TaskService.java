package com.ltphat.task_management.application.services;

import com.ltphat.task_management.application.dtos.TaskDto;
import com.ltphat.task_management.application.mappers.TaskMapper;
import com.ltphat.task_management.domain.model.Task;
import com.ltphat.task_management.domain.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDto> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return taskMapper.tasksToTaskDtos(tasks);
    }

    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTask(taskDto);
        task = taskRepository.save(task);
        return taskMapper.taskToTaskDto(task);
    }

    public Optional<TaskDto> getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(taskMapper::taskToTaskDto);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
