package com.ltphat.task_management.application.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.mappers.TaskMapper;
import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.model.Task;
import com.ltphat.task_management.domain.repository.TaskRepository;
import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;
    private Category category;
    private CategoryResponseDto categoryResponseDto;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category(1L, "Work", "Work related tasks", "blue", Instant.now());
        categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work related tasks", "blue", category.getCreatedAt());

        task = new Task(1L, "Task 1", "Description of Task 1", "Pending", category);
        taskRequestDto = new TaskRequestDto("Task 1", "Description of Task 1", "Pending",1L);
        taskResponseDto = new TaskResponseDto(1L, "Task 1", "Description of Task 1", "Pending", categoryResponseDto);
    }

    @Test
    @Order(1)
    void testCreateTask() {
        // Mocking the repository and mapper
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.taskRequestDtoToTask(any(TaskRequestDto.class))).thenReturn(task);
        when(taskMapper.taskToTaskResponseDto(any(Task.class))).thenReturn(taskResponseDto);

        // Call the service method
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto);

        // Verifying the result
        assertNotNull(createdTask);
        assertEquals("Task 1", createdTask.getName());
        assertEquals("Pending", createdTask.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));  // Verifying repository save method was called
    }

    @Test
    @Order(2)
    void testGetAllTasks() {
        // Arrange: Create a Pageable instance and a valid Page containing tasks
        Pageable pageable = PageRequest.of(0, 5); // Creates a Pageable with page 0, size 5
        List<Task> tasks = Arrays.asList(task); // A list of tasks
        PageImpl<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size()); // Creating PageImpl with tasks

        // Mocking the repository to return a Page of tasks when findAll is called
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        // Mocking the mapper to convert a task to TaskResponseDto
        when(taskMapper.taskToTaskResponseDto(any(Task.class))).thenReturn(taskResponseDto);

        // Act: Call the service method to get all tasks
        PagedResponseDto<TaskResponseDto> pagedResponseDto = taskService.getAllTasks("", "name", "asc", 0, 5);

        // Extracting the list of TaskResponseDto from the PagedResponseDto
        List<TaskResponseDto> taskDtos = pagedResponseDto.getContent();

        // Assert: Check that the result is not null and the content matches expectations
        assertNotNull(taskDtos);
        assertEquals(1, taskDtos.size());
        assertEquals("Task 1", taskDtos.get(0).getName());

        // Verify interactions with the mocks
        verify(taskRepository, times(1)).findAll(pageable); // Verify the repository's findAll method is called once
        verify(taskMapper, times(1)).taskToTaskResponseDto(any(Task.class)); // Verify the mapper's method is called once
    }

    @Test
    void testUpdateTask() {
        // Mocking the repository behavior
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.taskToTaskResponseDto(any(Task.class))).thenReturn(taskResponseDto);

        // Call the service method
        TaskResponseDto updatedTask = taskService.updateTask(1L, taskRequestDto);

        // Verifying the result
        assertNotNull(updatedTask);
        assertEquals("Task 1", updatedTask.getName());
        verify(taskRepository, times(1)).findById(1L);  // Verifying repository findById method was called
        verify(taskRepository, times(1)).save(any(Task.class));  // Verifying repository save method was called
    }

    @Test
    void testDeleteTask() {
        // Mocking repository behavior
        doNothing().when(taskRepository).deleteById(1L);

        // Call the service method
        taskService.deleteTask(1L);

        // Verifying the repository's delete method was called
        verify(taskRepository, times(1)).deleteById(1L);
    }
}

