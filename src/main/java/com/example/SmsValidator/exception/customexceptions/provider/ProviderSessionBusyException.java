package com.example.SmsValidator.exception.customexceptions.provider;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class ProviderSessionBusyException extends CustomException {
    public ProviderSessionBusyException(String message, Type type) {
        super(message, type);
    }
}
