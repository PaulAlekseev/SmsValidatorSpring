package com.example.SmsValidator.exception;

import lombok.Getter;

import java.lang.reflect.Type;

public class CustomException extends Exception {
    @Getter
    private final Type type;

    public CustomException(String message, Type type) {
        super(message);
        this.type = type;
    }
}
