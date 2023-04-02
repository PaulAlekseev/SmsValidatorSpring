package com.example.SmsValidator.bean.servicetype.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.entity.ServiceTypeEntity;
import lombok.Data;

@Data
public class CreateNewServiceSuccessResponse extends BaseResponse {
    private ServiceTypeEntity serviceType;
}
