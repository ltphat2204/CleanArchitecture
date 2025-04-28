package com.ltphat.task_management.application.mappers;

import com.ltphat.task_management.application.dtos.TaskDto;
import com.ltphat.task_management.domain.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDto taskToTaskDto(Task task);

    Task taskDtoToTask(TaskDto taskDto);

    List<TaskDto> tasksToTaskDtos(List<Task> tasks);
}
