package com.example.SmsValidator.exception.customexceptions.user;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class NotAllowedPasswordException extends CustomException {
    public NotAllowedPasswordException(String message, Type type) {
        super(message, type);
    }
}
