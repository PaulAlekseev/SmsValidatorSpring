package com.example.SmsValidator.bean.authentication.providerfactory;


import java.lang.reflect.Type;

public interface ValidationProviderFactory {
    String getSecretKey();

    String getUrl();

    Type getType();
}
