package com.ltphat.task_management.interfaces.api;

import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import com.ltphat.task_management.application.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {
    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskRestController;

    private MockMvc mockMvc;

    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskRestController).build();

        taskRequestDto = new TaskRequestDto("Task 1", "Description of Task 1", "Pending");
        taskResponseDto = new TaskResponseDto(1L, "Task 1", "Description of Task 1", "Pending");
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(taskResponseDto);

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content("{\"name\":\"Task 1\", \"description\":\"Description of Task 1\", \"status\":\"Pending\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Task 1"))
                .andExpect(jsonPath("$.status").value("Pending"));

        verify(taskService, times(1)).createTask(any(TaskRequestDto.class));
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Mocking the service response
        PagedResponseDto<TaskResponseDto> pagedResponseDto = new PagedResponseDto<>(
                List.of(taskResponseDto),  // List of task DTOs
                0,  // currentPage
                1,  // totalPages
                1   // totalItems
        );

        // Mock the service call
        when(taskService.getAllTasks("", "name", "asc", 0, 5)).thenReturn(pagedResponseDto);

        // Perform the GET request to /tasks with query parameters
        mockMvc.perform(get("/tasks?page=0&size=5"))
                .andExpect(status().isOk())  // Check for OK response
                .andExpect(jsonPath("$.content[0].name").value("Task 1"))  // Check task name in content
                .andExpect(jsonPath("$.content[0].status").value("Pending"))  // Check task status in content
                .andExpect(jsonPath("$.currentPage").value(0))  // Check currentPage
                .andExpect(jsonPath("$.totalPages").value(1))  // Check totalPages
                .andExpect(jsonPath("$.totalItems").value(1));  // Check totalItems

        // Verify that the service method was called exactly once
        verify(taskService, times(1)).getAllTasks("", "name", "asc", 0, 5);
    }
}
