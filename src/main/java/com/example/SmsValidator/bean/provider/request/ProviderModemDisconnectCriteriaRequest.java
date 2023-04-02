package com.example.SmsValidator.bean.provider.request;

import lombok.Data;

@Data
public class ProviderModemDisconnectCriteriaRequest {
    private boolean byRevenue = false;
    private int revenue = 0;
    private boolean byService = false;
    private String services = "";
}
