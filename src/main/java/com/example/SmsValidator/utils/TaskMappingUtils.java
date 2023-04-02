package com.example.SmsValidator.utils;

import com.example.SmsValidator.dto.task.TaskBaseDto;
import com.example.SmsValidator.dto.task.TaskForUserDto;
import com.example.SmsValidator.entity.TaskEntity;

public class TaskMappingUtils {

    public static TaskBaseDto mapToBaseTaskDto(TaskEntity taskEntity) {
        TaskBaseDto taskBaseDto = new TaskBaseDto();
        taskBaseDto.setId(taskEntity.getId());
        taskBaseDto.setServiceName(taskEntity.getServiceName());
        taskBaseDto.setSuccess(taskEntity.getSuccess());
        taskBaseDto.setDone(taskEntity.getDone());
        taskBaseDto.setCost(taskEntity.getCost());
        return taskBaseDto;
    }

    public static TaskForUserDto mapToTaskForUserDto(TaskEntity taskEntity, String phoneNumber) {
        TaskForUserDto taskForUserDto = new TaskForUserDto();
        taskForUserDto.setId(taskEntity.getId());
        taskForUserDto.setReady(taskEntity.getReady());
        taskForUserDto.setDone(taskEntity.getDone());
        taskForUserDto.setTimeSeconds(taskEntity.getTimeSeconds());
        taskForUserDto.setPhoneNumber(phoneNumber);
        taskForUserDto.setMessages(
                taskEntity.getMessages()
                        .stream()
                        .map(MessageMappingUtils::mapToMessageBaseDto)
                        .toList()
        );
        return taskForUserDto;
    }
}
