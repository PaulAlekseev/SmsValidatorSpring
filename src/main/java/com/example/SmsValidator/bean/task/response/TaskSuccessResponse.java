package com.example.SmsValidator.bean.task.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.modem.ModemForUserDto;
import com.example.SmsValidator.dto.task.TaskForUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSuccessResponse extends BaseResponse {
    private ModemForUserDto modem;
    private TaskForUserDto task;
}
