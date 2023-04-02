package com.example.SmsValidator.exception.customexceptions.socket;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class NoMatchingPatternMessagesException extends CustomException {
    public NoMatchingPatternMessagesException(String message, Type type) {
        super(message, type);
    }
}
