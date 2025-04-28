package com.ltphat.task_management.application.dtos.task;

import lombok.Data;

@Data
public class TaskRequestDto {
    private String name;
    private String description;
    private String status; // Pending, Completed
}