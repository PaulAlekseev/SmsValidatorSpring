package com.example.SmsValidator.dto.task;

import lombok.Data;

@Data
public class TaskBaseDto {
    private Long id;
    private String serviceName;
    private Float cost;
    private Boolean done;
    private Boolean success;
}
