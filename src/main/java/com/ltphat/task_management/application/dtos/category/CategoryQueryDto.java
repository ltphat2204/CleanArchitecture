package com.ltphat.task_management.application.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryQueryDto {
    private Integer page = 0;
    private Integer pageSize = 10;
    private String search = "";
    private String sortBy = "name";
    private String sortOrder = "asc";
}
