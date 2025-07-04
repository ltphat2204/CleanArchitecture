package com.ltphat.task_management.interfaces.api;

import com.ltphat.task_management.application.dtos.category.CategoryCreateDto;
import com.ltphat.task_management.application.dtos.category.CategoryQueryDto;
import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.category.CategoryUpdateDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public PagedResponseDto<CategoryResponseDto> getCategoriesController(CategoryQueryDto categoryQueryDto) {

        return categoryService.getCategories(categoryQueryDto);
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryByIDController(@PathVariable Long id){
        return categoryService.getCategoryByID(id);
    }

    @PostMapping()
    public CategoryResponseDto createCategoryController(@RequestBody @Valid CategoryCreateDto categoryCreateDto){
        return categoryService.createCategory(categoryCreateDto);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto updateCategoryController (@PathVariable Long id, @RequestBody CategoryUpdateDto categoryUpdateDto){
        return categoryService.updateCategory(id, categoryUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategoryController(@PathVariable Long id){
        categoryService.deleteCategory(id);
    }
}
