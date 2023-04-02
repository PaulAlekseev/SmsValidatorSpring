package com.example.SmsValidator.bean.refreshtoken.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenSuccessResponse extends BaseResponse {
    private String authToken;
}
