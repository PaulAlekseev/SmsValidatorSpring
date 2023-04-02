package com.example.SmsValidator.config;

import com.example.SmsValidator.bean.mail.MailInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailInfoProvider mailInfoProvider;

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailInfoProvider.getHost());
        mailSender.setPort(mailInfoProvider.getPort());
        mailSender.setUsername(mailInfoProvider.getUsername());
        mailSender.setPassword(mailInfoProvider.getPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", mailInfoProvider.getProtocol());
        properties.setProperty("mail.debug", mailInfoProvider.getDebug());
        return mailSender;
    }

}
