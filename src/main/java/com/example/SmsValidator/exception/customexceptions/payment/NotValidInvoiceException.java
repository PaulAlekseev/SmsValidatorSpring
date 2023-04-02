package com.example.SmsValidator.exception.customexceptions.payment;

import com.example.SmsValidator.exception.CustomException;

import java.lang.reflect.Type;

public class NotValidInvoiceException extends CustomException {
    public NotValidInvoiceException(String message, Type type) {
        super(message, type);
    }
}
