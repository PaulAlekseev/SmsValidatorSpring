package com.example.SmsValidator.bean.user;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.Data;

@Data
public class UserInfoResponse extends BaseResponse {
    private final String username;
    private final double balance;
}
