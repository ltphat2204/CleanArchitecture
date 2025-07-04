package com.ltphat.task_management.application.dtos.task;

import com.ltphat.task_management.application.dtos.category.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String name;
    private String description;
    private String status; // Pending,
    private CategoryResponseDto category;
}
