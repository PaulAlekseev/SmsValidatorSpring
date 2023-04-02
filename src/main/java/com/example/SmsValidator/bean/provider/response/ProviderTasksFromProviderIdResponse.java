package com.example.SmsValidator.bean.provider.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.task.TaskBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderTasksFromProviderIdResponse extends BaseResponse {
    private List<TaskBaseDto> tasks;
}
