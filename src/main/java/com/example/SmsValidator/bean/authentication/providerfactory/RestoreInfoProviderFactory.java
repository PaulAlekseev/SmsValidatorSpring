package com.example.SmsValidator.bean.authentication.providerfactory;

import com.example.SmsValidator.bean.authentication.RestorePasswordData;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@Getter
public class RestoreInfoProviderFactory implements ValidationProviderFactory {
    @Value("${auth.validation.restore.secretKey}")
    private String secretKey;

    @Value("${auth.validation.restore.url}")
    private String url;

    @Override
    public Type getType() {
        return RestorePasswordData.class;
    }
}
