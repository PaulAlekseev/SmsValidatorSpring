package com.example.SmsValidator.bean.payment.coinremitter.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinRemitterSuccessResponse extends BaseResponse {
    private String url;
    private String address;
    private String qrCode;
    private String coin;
    private String amountInCoin;
    private String amountInUSD;
}
