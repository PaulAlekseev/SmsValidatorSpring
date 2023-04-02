package com.example.SmsValidator.exception.customexceptions.user;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class RequestIsOutdatedException extends CustomException {
    public RequestIsOutdatedException(String message, Type type) {
        super(message, type);
    }
}
