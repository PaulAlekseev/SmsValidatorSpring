package com.example.SmsValidator.socket.container;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckProviderOutContainer {
    private final Long taskId;
    private final String portName;
}
