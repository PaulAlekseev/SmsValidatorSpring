package com.example.SmsValidator.exception.customexceptions.provider;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class CouldNotFindSuchModemException extends CustomException {
    public CouldNotFindSuchModemException(String message, Type type) {
        super(message, type);
    }
}
