package com.ltphat.task_management.application.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDto {
    private String name;
    private String description;
    private String Color;
}
