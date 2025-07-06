package com.ltphat.task_management.application.mappers;

import com.ltphat.task_management.application.dtos.category.CategoryCreateDto;
import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import com.ltphat.task_management.application.dtos.category.CategoryUpdateDto;
import com.ltphat.task_management.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    CategoryResponseDto toCategoryResponseDTO(Category category);

    Category toCategory(CategoryCreateDto categoryCreateDto);

    void updateFromDto(CategoryUpdateDto dto, @MappingTarget Category entity);
}
