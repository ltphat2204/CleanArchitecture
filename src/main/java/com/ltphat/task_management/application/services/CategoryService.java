package com.ltphat.task_management.application.services;

import com.ltphat.task_management.application.dtos.category.CategoryCreateDto;
import com.ltphat.task_management.application.dtos.category.CategoryQueryDto;
import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.category.CategoryUpdateDto;
import com.ltphat.task_management.application.dtos.shared.PagedResponseDto;
import com.ltphat.task_management.application.mappers.CategoryMapper;
import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto){
        log.info(categoryCreateDto.toString());
        Category category = categoryMapper.toCategory(categoryCreateDto);
        try{
            category = categoryRepository.save(category);
        }catch(Exception exception){
            log.error("error while saving category " +exception);
            throw new RuntimeException("Error while create category!");
        }
        return categoryMapper.toCategoryResponseDTO(category);
    }

    public CategoryResponseDto getCategoryByID(Long id){
        if(id == null || id <= 0){
            throw new RuntimeException("The id is invalid");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toCategoryResponseDTO(category);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto){
        if(id == null || id <= 0){
            throw new RuntimeException("The id is invalid");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryMapper.updateFromDto(categoryUpdateDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id){
        if(id == null || id <= 0){
            throw new RuntimeException("The id is invalid");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        categoryRepository.deleteById(id);
    }

    //Default pageSize is 20 (pageAble)
    //If search query is empty, this method will take all categories with pagination
    public PagedResponseDto<CategoryResponseDto> getCategories(CategoryQueryDto categoryQueryDto){
        String search = categoryQueryDto.getSearch();
        String sortOrder = categoryQueryDto.getSortOrder();
        String sortBy = categoryQueryDto.getSortBy();
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy));

        Pageable pageable = PageRequest.of(categoryQueryDto.getPage(), categoryQueryDto.getPageSize(), sort);
        Page<Category> categoryPage;
        if(search.isEmpty()){
            categoryPage = categoryRepository.findAll(pageable);
        }
        else{
            categoryPage = categoryRepository.findCategoryByName(search, pageable);
        }
        if(categoryPage == null){
            return new PagedResponseDto<>(Collections.emptyList(), 1, 1, 0);
        }
        List<CategoryResponseDto> result = categoryPage.map(categoryMapper::toCategoryResponseDTO).getContent();
        return new PagedResponseDto<>(
                result,
                categoryPage.getNumber(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    };
}
