package com.example.SmsValidator.bean.user;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.task.TaskForUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserTaskHistoryResponse extends BaseResponse {
    private final List<TaskForUserDto> tasks;
}
