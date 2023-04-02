package com.example.SmsValidator.bean.payment.coinremitter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomData {
    private Long id;
    private String email;
    private Float amount;
}
