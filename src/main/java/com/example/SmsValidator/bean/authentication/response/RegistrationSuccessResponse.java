package com.example.SmsValidator.bean.authentication.response;

import com.example.SmsValidator.bean.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSuccessResponse extends BaseResponse {

    private String message;
}
