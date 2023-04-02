package com.example.SmsValidator.exception.customexceptions.socket;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class ModemProviderSessionAlreadyActiveException extends CustomException {
    public ModemProviderSessionAlreadyActiveException(String message, Type type) {
        super(message, type);
    }
}
