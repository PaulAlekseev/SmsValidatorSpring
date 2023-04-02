package com.example.SmsValidator.bean.servicetype.request;

import lombok.Data;

@Data
public class CreateNewServiceRequest {
    private String name;
    private int allowedAmount;
    private int daysBetween;
    private String messageRegex;
    private String senderRegex;
    private Long timeSeconds;
}
