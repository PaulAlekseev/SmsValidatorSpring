package com.example.SmsValidator.bean.servicetype.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.service.ServiceTypeBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class GetAllowedServiceTypesResponse extends BaseResponse {
    private final List<ServiceTypeBaseDto> services;
    private final int amount;

    public GetAllowedServiceTypesResponse(List<ServiceTypeBaseDto> services) {
        this.services = services;
        this.amount = services.size();
    }
}
