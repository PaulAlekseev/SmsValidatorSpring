package com.example.SmsValidator.exception.customexceptions.user;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class UserAlreadyExistException extends CustomException {
    public UserAlreadyExistException(String message, Type type) {
        super(message, type);
    }
}
