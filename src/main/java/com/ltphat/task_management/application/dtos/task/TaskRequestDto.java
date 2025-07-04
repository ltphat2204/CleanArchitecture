package com.ltphat.task_management.application.dtos.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDto {
    private String name;
    private String description;
    private String status; // Pending,
    private Long categoryId;
}