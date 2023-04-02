package com.example.SmsValidator.dto.task;

import com.example.SmsValidator.dto.message.MessageBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class TaskForUserDto {
    private Long id;
    private String phoneNumber;
    private Boolean ready;
    private Boolean done;
    private Long timeSeconds;
    private List<MessageBaseDto> messages;
}
