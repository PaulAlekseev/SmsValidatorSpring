package com.example.SmsValidator.dto.service;

import lombok.Data;

@Data
public class ServiceTypeBaseDto {
    private Long id;
    private String name;
    private Integer allowedAmount;
    private Integer daysBetween;
    private Float cost;
}
