package com.example.SmsValidator.dto.modem;

import lombok.Data;

import java.util.Date;

@Data
public class ModemBaseDto {
    private Long id;
    private String phoneNumber;
    private String IMSI;
    private String ICCID;
    private Boolean busy;
    private Date reservedUntil;
    private String services;
}
