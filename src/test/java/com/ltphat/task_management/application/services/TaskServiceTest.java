package com.ltphat.task_management.application.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import com.ltphat.task_management.application.mappers.TaskMapper;
import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.model.Task;
import com.ltphat.task_management.domain.repository.CategoryRepository;
import com.ltphat.task_management.domain.repository.TaskRepository;
import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;
    private Category category;
    private CategoryResponseDto categoryResponseDto;
    @BeforeEach
    void setUp() {
        category = new Category(1L, "Work", "Work related tasks", "blue", Instant.now());
        categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work related tasks", "blue", category.getCreatedAt());

        task = new Task(1L, "Task 1", "Description of Task 1", "Pending", category);
        taskRequestDto = new TaskRequestDto("Task 1", "Description of Task 1", "Pending",categoryResponseDto.getId());
        taskResponseDto = new TaskResponseDto(1L, "Task 1", "Description of Task 1", "Pending", categoryResponseDto);
    }

    @Test
    void testCreateTask_Success() {
        // Mocking the repository and
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
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
    void testCreateTask_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, ()->{taskService.createTask(taskRequestDto);});
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetAllTasks_Success_NoSearch() {
        // Arrange: Create a Pageable instance and a valid Page containing tasks
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(0, 5, sort); // Creates a Pageable with page 0, size 5
        List<Task> tasks = Arrays.asList(task); // A list of tasks
        PageImpl<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size()); // Creating PageImpl with tasks

        // Mocking the repository to return a Page of tasks when findAll is called
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        log.info(taskPage.toString());
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
    void testGetAllTasks_Success_NullSearch() {
        // Arrange: Create a Pageable instance and a valid Page containing tasks
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(0, 5, sort); // Creates a Pageable with page 0, size 5
        List<Task> tasks = Arrays.asList(task); // A list of tasks
        PageImpl<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size()); // Creating PageImpl with tasks

        // Mocking the repository to return a Page of tasks when findAll is called
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        log.info(taskPage.toString());
        // Mocking the mapper to convert a task to TaskResponseDto
        when(taskMapper.taskToTaskResponseDto(any(Task.class))).thenReturn(taskResponseDto);

        // Act: Call the service method to get all tasks
        PagedResponseDto<TaskResponseDto> pagedResponseDto = taskService.getAllTasks(null, "name", "asc", 0, 5);

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
    void testGetAllTasks_Success_WithSearch(){
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(0, 5, sort); // Creates a Pageable with page 0, size 5
        List<Task> tasks = Arrays.asList(task); // A list of tasks
        PageImpl<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findByNameContainingIgnoreCase("Task 1",pageable)).thenReturn(taskPage);
        when(taskMapper.taskToTaskResponseDto(any(Task.class))).thenReturn(taskResponseDto);

        PagedResponseDto<TaskResponseDto> pagedResponseDto = taskService.getAllTasks("Task 1", "name", "asc", 0, 5);

        List<TaskResponseDto> taskDtos = pagedResponseDto.getContent();

        assertNotNull(taskDtos);
        assertEquals(1, taskDtos.size());
        assertEquals("Task 1", taskDtos.get(0).getName());

        verify(taskRepository, times(1)).findByNameContainingIgnoreCase("Task 1",pageable); // Verify the repository's findAll method is called once
        verify(taskMapper, times(1)).taskToTaskResponseDto(any(Task.class)); // Verify the mapper's method is called once
    }

    @Test
    void testGetAllTasks_Success_EmptyResult(){
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(0, 5, sort); // Creates a Pageable with page 0, size 5
        PageImpl<Task> taskPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(taskRepository.findByNameContainingIgnoreCase("Task 1",pageable)).thenReturn(taskPage);

        PagedResponseDto<TaskResponseDto> pagedResponseDto = taskService.getAllTasks("Task 1", "name", "asc", 0, 5);

        List<TaskResponseDto> taskDtos = pagedResponseDto.getContent();

        assertNotNull(taskDtos);
        assertEquals(0, taskDtos.size());

        verify(taskRepository, times(1)).findByNameContainingIgnoreCase("Task 1",pageable); // Verify the repository's findAll method is called once
        verify(taskMapper, never()).taskToTaskResponseDto(any(Task.class)); // Verify the mapper's method is called once
    }

    @Test
    void testUpdateTask_Success() {
        // Mocking the repository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
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
    void testUpdateTask_TaskNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, ()->{taskService.updateTask(1L, taskRequestDto);});
        verify(taskRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_CategoryNotFound(){
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, ()->{taskService.updateTask(1L, taskRequestDto);});

        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    void testDeleteTask_Success() {
        // Mocking repository behavior
        doNothing().when(taskRepository).deleteById(1L);

        // Call the service method
        taskService.deleteTask(1L);

        // Verifying the repository's delete method was called
        verify(taskRepository, times(1)).deleteById(1L);
    }
}

