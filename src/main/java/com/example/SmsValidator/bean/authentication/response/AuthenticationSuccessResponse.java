package com.example.SmsValidator.bean.authentication.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuthenticationSuccessResponse extends BaseResponse {
    private String authToken;
    private String refreshToken;
}
