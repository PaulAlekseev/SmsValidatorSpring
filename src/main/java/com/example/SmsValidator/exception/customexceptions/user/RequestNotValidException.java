package com.example.SmsValidator.exception.customexceptions.user;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class RequestNotValidException extends CustomException {
    public RequestNotValidException(String message, Type type) {
        super(message, type);
    }
}
