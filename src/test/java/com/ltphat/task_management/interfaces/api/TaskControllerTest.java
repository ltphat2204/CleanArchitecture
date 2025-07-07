package com.ltphat.task_management.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import com.ltphat.task_management.application.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @MockitoBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;
    CategoryResponseDto categoryResponseDto;
    @BeforeEach
    void setUp() {
        categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work tasks", "blue", Instant.now());

        taskRequestDto = new TaskRequestDto("Task 1", "Description of Task 1", "Pending", categoryResponseDto.getId());
        taskResponseDto = new TaskResponseDto(1L, "Task 1", "Description of Task 1", "Pending", categoryResponseDto);
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(taskResponseDto);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Task 1"))
                .andExpect(jsonPath("$.status").value("Pending"));

        verify(taskService, times(1)).createTask(any(TaskRequestDto.class));
    }

    @Test
    void testGetAllTasks() throws Exception {
        PagedResponseDto<TaskResponseDto> pagedResponseDto = new PagedResponseDto<>(
                List.of(taskResponseDto),
                0,  // currentPage
                1,  // totalPages
                1   // totalItems
        );

        when(taskService.getAllTasks(
                isNull(),
                eq("name"),
                eq("asc"),
                eq(0),
                eq(5))
        ).thenReturn(pagedResponseDto);


        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Task 1"))
                .andExpect(jsonPath("$.items[0].status").value("Pending"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalItems").value(1));

        verify(taskService).getAllTasks(isNull(), eq("name"), eq("asc"), eq(0), eq(5));
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);
        PagedResponseDto<TaskResponseDto> pagedResponseDto = new PagedResponseDto<>(
                List.of(taskResponseDto),
                0,  // currentPage
                1,  // totalPages
                1   // totalItems
        );

        when(taskService.getAllTasks(
                isNull(),
                eq("name"),
                eq("asc"),
                eq(0),
                eq(5))
        ).thenReturn(pagedResponseDto);


        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Task 1"))
                .andExpect(jsonPath("$.items[0].status").value("Pending"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalItems").value(1));

        verify(taskService).getAllTasks(isNull(), eq("name"), eq("asc"), eq(0), eq(5));

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        Long nonExistentTaskId = 99L;
        doThrow(new RuntimeException("Task not found with id: " + nonExistentTaskId))
                .when(taskService).deleteTask(nonExistentTaskId);

        mockMvc.perform(delete("/tasks/{id}", nonExistentTaskId))
                .andExpect(status().isInternalServerError());

        verify(taskService, times(1)).deleteTask(nonExistentTaskId);
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        Long taskId = 1L;
        TaskRequestDto updateRequest = new TaskRequestDto("Updated Task", "Updated Description", "Completed", categoryResponseDto.getId());
        TaskResponseDto updatedResponse = new TaskResponseDto(taskId, "Updated Task", "Updated Description", "Completed", categoryResponseDto);

        when(taskService.updateTask(eq(taskId), any(TaskRequestDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("Completed"));

        verify(taskService, times(1)).updateTask(eq(taskId), any(TaskRequestDto.class));
    }

    @Test
    void testUpdateTask_NotFound() throws Exception {
        Long nonExistentTaskId = 99L;
        TaskRequestDto updateRequest = new TaskRequestDto("Task", "Description", "Pending", 1L);

        when(taskService.updateTask(eq(nonExistentTaskId), any(TaskRequestDto.class)))
                .thenThrow(new RuntimeException("Task not found with id: " + nonExistentTaskId));

        mockMvc.perform(put("/tasks/{id}", nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        verify(taskService, times(1)).updateTask(eq(nonExistentTaskId), any(TaskRequestDto.class));
    }


}
