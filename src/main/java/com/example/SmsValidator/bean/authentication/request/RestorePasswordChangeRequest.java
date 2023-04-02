package com.example.SmsValidator.bean.authentication.request;

import lombok.Data;

@Data
public class RestorePasswordChangeRequest {
    private String newPassword;
}
