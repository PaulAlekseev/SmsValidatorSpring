package com.example.SmsValidator.bean.payment.coinremitter.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.Data;

@Data
public class CoinRemitterSiteResponse extends BaseResponse {
    private int flag;
    private String msg;

    private CoinRemitterSiteResponseData data;

}
