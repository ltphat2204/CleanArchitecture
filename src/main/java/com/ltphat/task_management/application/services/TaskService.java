package com.ltphat.task_management.application.services;

import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import com.ltphat.task_management.application.mappers.CategoryMapper;
import com.ltphat.task_management.application.mappers.TaskMapper;
import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.model.Task;
import com.ltphat.task_management.domain.repository.CategoryRepository;
import com.ltphat.task_management.domain.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.Collections;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskMapper taskMapper;

    public PagedResponseDto<TaskResponseDto> getAllTasks(String search, String sortBy, String sortOrder, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy));

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasksPage;

        if (search == null || search.isEmpty()) {
            tasksPage = taskRepository.findAll(pageable);
        } else {
            tasksPage = taskRepository.findByNameContainingIgnoreCase(search, pageable);
        }

        if (tasksPage == null) {
            return new PagedResponseDto<>(Collections.emptyList(), 1, 1, 0);
        }

        List<TaskResponseDto> taskDtos = tasksPage.map(taskMapper::taskToTaskResponseDto).getContent();

        return new PagedResponseDto<>(
                taskDtos,
                tasksPage.getNumber(),
                tasksPage.getTotalPages(),
                tasksPage.getTotalElements()
        );
    }

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Category category = categoryRepository.findById(taskRequestDto.getCategoryId()).
                    orElseThrow(()-> new RuntimeException("Category not found with id: " + taskRequestDto.getCategoryId()));
        Task task = taskMapper.taskRequestDtoToTask(taskRequestDto);
        task.setCategory(category);
        task = taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        Category category = categoryRepository.findById(taskRequestDto.getCategoryId()).
                orElseThrow(()-> new RuntimeException("Category not found with id: " + taskRequestDto.getCategoryId()));

        task.setName(taskRequestDto.getName());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(taskRequestDto.getStatus());
        task.setCategory(category);
        task = taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
