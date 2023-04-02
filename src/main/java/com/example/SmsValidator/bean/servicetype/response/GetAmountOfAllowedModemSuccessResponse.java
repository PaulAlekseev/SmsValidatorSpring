package com.example.SmsValidator.bean.servicetype.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAmountOfAllowedModemSuccessResponse extends BaseResponse {

    private final String service;
    private final int amount;
}
