package com.example.SmsValidator.exception.customexceptions.modem;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class ModemNotFoundException extends CustomException {
    public ModemNotFoundException(String message, Type type) {
        super(message, type);
    }
}
