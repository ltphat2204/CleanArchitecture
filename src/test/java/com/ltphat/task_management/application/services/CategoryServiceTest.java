package com.ltphat.task_management.application.services;

import com.ltphat.task_management.application.dtos.category.*;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.mappers.CategoryMapper;
import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryCreateDto categoryCreateDto;
    private CategoryResponseDto categoryResponseDto;
    private CategoryUpdateDto categoryUpdateDto;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        category = new Category(1L, "Work", "Work related tasks", "blue", Instant.now());
        categoryResponseDto = new CategoryResponseDto(1L, "Work", "Work related tasks", "blue", category.getCreatedAt());
    }

    @Test
    void createCategory_shouldReturnCategoryResponseDto_whenSuccessful() {
        CategoryCreateDto createDto = new CategoryCreateDto("Work", "Work related tasks");
        when(categoryMapper.toCategory(any(CategoryCreateDto.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.createCategory(createDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Work");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getCategoryByID_shouldReturnDto_whenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponseDTO(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.getCategoryByID(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCategoryByID_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryByID(99L);
        });
        assertThat(exception.getMessage()).isEqualTo("Category not found with id: 99");
    }

    @Test
    void getCategoryByID_shouldThrowException_whenIdIsInvalid() {
        assertThrows(RuntimeException.class, () -> categoryService.getCategoryByID(null));
        assertThrows(RuntimeException.class, () -> categoryService.getCategoryByID(0L));
        assertThrows(RuntimeException.class, () -> categoryService.getCategoryByID(-1L));
    }


    @Test
    void updateCategory_shouldReturnUpdatedDto_whenCategoryExists() {
        CategoryUpdateDto updateDto = new CategoryUpdateDto("Updated Name", "Updated Desc", "red");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryResponseDto.setName("Updated Name");
        when(categoryMapper.toCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.updateCategory(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).updateFromDto(updateDto, category);
    }

    @Test
    void updateCategory_shouldThrowException_whenCategoryNotFound() {
        CategoryUpdateDto updateDto = new CategoryUpdateDto();
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(99L, updateDto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_shouldCallDeleteById_whenCategoryExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(99L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void getCategories_shouldReturnPagedResult_whenNoSearch() {
        CategoryQueryDto queryDto = new CategoryQueryDto();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Category> categoryList = Collections.singletonList(category);
        Page<Category> categoryPage = new PageImpl<>(categoryList, pageable, 1);

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDto);

        PagedResponseDto<CategoryResponseDto> result = categoryService.getCategories(queryDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
        verify(categoryRepository, never()).findCategoryByName(anyString(), any(Pageable.class));
    }

    @Test
    void getCategories_shouldCallFindByName_whenSearchIsProvided() {
        CategoryQueryDto queryDto = new CategoryQueryDto();
        queryDto.setSearch("Work");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category), pageable, 1);

        when(categoryRepository.findCategoryByName("Work", pageable)).thenReturn(categoryPage);
        when(categoryMapper.toCategoryResponseDTO(any(Category.class))).thenReturn(categoryResponseDto);

        PagedResponseDto<CategoryResponseDto> result = categoryService.getCategories(queryDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(categoryRepository, never()).findAll(any(Pageable.class));
        verify(categoryRepository, times(1)).findCategoryByName("Work", pageable);
    }
}
