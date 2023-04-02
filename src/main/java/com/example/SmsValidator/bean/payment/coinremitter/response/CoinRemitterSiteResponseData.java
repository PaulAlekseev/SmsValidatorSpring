package com.example.SmsValidator.bean.payment.coinremitter.response;

import lombok.Data;

@Data
public class CoinRemitterSiteResponseData {
    private String url;
    private String address;
    private String coin;
    private String usd_amount;
}
