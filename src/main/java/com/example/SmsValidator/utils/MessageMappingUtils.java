package com.example.SmsValidator.utils;

import com.example.SmsValidator.dto.message.MessageBaseDto;
import com.example.SmsValidator.entity.MessageEntity;

public class MessageMappingUtils {
    public static MessageBaseDto mapToMessageBaseDto(MessageEntity messageEntity) {
        MessageBaseDto messageBaseDto = new MessageBaseDto();
        messageBaseDto.setMessage(messageEntity.getMessage());
        messageBaseDto.setSender(messageEntity.getSender());
        return messageBaseDto;
    }
}
