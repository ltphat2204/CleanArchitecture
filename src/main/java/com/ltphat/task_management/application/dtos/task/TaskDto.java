package com.ltphat.task_management.application.dtos.task;

import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private String status; // Pending, Completed

}