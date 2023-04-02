package com.example.SmsValidator.bean.authentication.providerfactory;

import com.example.SmsValidator.bean.authentication.ValidationData;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@Getter
public class VerifyInfoProviderFactory implements ValidationProviderFactory {
    @Value("${auth.validation.verify.secretKey}")
    private String secretKey;

    @Value("${auth.validation.verify.url}")
    private String url;

    @Override
    public Type getType() {
        return ValidationData.class;
    }
}
