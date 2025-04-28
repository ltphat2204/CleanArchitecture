package com.ltphat.task_management.application.mappers;

import com.ltphat.task_management.application.dtos.task.TaskRequestDto;
import com.ltphat.task_management.application.dtos.task.TaskResponseDto;
import com.ltphat.task_management.domain.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskResponseDto taskToTaskResponseDto(Task task);

    Task taskRequestDtoToTask(TaskRequestDto taskRequestDto);

}
