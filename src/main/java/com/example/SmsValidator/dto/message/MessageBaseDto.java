package com.example.SmsValidator.dto.message;

import lombok.Data;

@Data
public class MessageBaseDto {
    private String sender;

    private String message;
}
