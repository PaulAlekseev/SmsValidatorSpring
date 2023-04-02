package com.example.SmsValidator.bean.mail;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MailInfoProvider {

    @Value("${mail.validation.smtp.username}")
    private String username;

    @Value("${mail.validation.smtp.password}")
    private String password;

    @Value("${mail.validation.smtp.host}")
    private String host;

    @Value("${mail.validation.smtp.port}")
    private int port;

    @Value("${mail.validation.smtp.protocol}")
    private String protocol;

    @Value("${mail.debug}")
    private String debug;
}
