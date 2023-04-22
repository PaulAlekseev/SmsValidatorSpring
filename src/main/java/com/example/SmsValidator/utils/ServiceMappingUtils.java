package com.example.SmsValidator.utils;

import com.example.SmsValidator.dto.service.ServiceTypeBaseDto;
import com.example.SmsValidator.entity.ServiceTypeEntity;

public class ServiceMappingUtils {
    public static ServiceTypeBaseDto mapToServiceDto(ServiceTypeEntity serviceTypeEntity) {
        ServiceTypeBaseDto serviceBaseDto = new ServiceTypeBaseDto();
        serviceBaseDto.setId(serviceTypeEntity.getId());
        serviceBaseDto.setName(serviceTypeEntity.getName());
        serviceBaseDto.setAllowedAmount(serviceTypeEntity.getAllowedAmount());
        serviceBaseDto.setDaysBetween(serviceTypeEntity.getDaysBetween());
        serviceBaseDto.setCost(serviceTypeEntity.getCost());
        return serviceBaseDto;
    }
}
