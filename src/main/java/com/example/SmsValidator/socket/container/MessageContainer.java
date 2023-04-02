package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.entity.MessageEntity;
import lombok.Data;

import java.util.List;

@Data
public class MessageContainer {
    private Long taskId;
    private Long timeSeconds;
    private List<MessageEntity> messages;
}
