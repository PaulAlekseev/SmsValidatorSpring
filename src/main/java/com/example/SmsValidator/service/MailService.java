package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.mail.MailInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailInfoProvider mailInfoProvider;
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        String from = mailInfoProvider.getUsername();
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(from);
        mailMessage.setSubject(subject);
        mailMessage.setTo(to);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
    }
}
