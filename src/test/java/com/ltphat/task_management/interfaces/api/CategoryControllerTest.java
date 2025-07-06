package com.ltphat.task_management.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ltphat.task_management.application.dtos.category.CategoryCreateDto;
import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.category.CategoryUpdateDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .build();

        categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work tasks", "blue", Instant.now());
    }

    @Test
    void getCategoriesController_shouldReturnPagedResponse() throws Exception {
        PagedResponseDto<CategoryResponseDto> pagedResponse = new PagedResponseDto<>(
                Collections.singletonList(categoryResponseDto), 0, 1, 1L);
        when(categoryService.getCategories(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Work"));
    }

    @Test
    void getCategoryByIDController_shouldReturnCategoryDto() throws Exception {
        when(categoryService.getCategoryByID(1L)).thenReturn(categoryResponseDto);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createCategoryController_shouldReturnCreatedCategoryDto() throws Exception {
        CategoryCreateDto createDto = new CategoryCreateDto("Work", "Work tasks");
        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(categoryResponseDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateCategoryController_shouldReturnUpdatedCategoryDto() throws Exception {
        Long categoryId = 1L;
        CategoryUpdateDto updateDto = new CategoryUpdateDto("Updated Name", null, "red");
        categoryResponseDto.setName("Updated Name");

        when(categoryService.updateCategory(eq(categoryId), any(CategoryUpdateDto.class))).thenReturn(categoryResponseDto);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteCategoryController_shouldReturnOk() throws Exception {
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isOk());
    }
}