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

    @BeforeEach
    void setUp() {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work tasks", "blue", Instant.now());

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

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(taskId);
    }

}
