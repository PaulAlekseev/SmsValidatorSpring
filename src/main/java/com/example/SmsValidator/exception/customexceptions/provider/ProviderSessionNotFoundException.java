package com.example.SmsValidator.exception.customexceptions.provider;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class ProviderSessionNotFoundException extends CustomException {
    public ProviderSessionNotFoundException(String message, Type type) {
        super(message, type);
    }
}
