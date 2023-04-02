package com.example.SmsValidator.bean.authentication;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RestorePasswordData {
    private String email;
    private Long userId;
    private Date lastUpdated;
    private Date requestCreated;
}
