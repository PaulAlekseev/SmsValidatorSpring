package com.example.SmsValidator.bean.reservemodem;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReserveModemCostProvider {

    @Getter
    @Value("${reserve-modem.cost}")
    private Float cost;
}
