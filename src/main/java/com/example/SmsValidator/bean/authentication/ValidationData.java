package com.example.SmsValidator.bean.authentication;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ValidationData {
    private String email;
    private Long id;
    private Date created;
}
