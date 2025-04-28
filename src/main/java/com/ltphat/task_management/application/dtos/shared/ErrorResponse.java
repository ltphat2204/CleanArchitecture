package com.ltphat.task_management.application.dtos.shared;

import lombok.Data;

@Data
public class ErrorResponse {

    private int status;
    private String message;
    private long timestamp;

}
