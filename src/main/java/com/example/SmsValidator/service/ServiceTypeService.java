package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.servicetype.response.GetAllowedServiceTypesResponse;
import com.example.SmsValidator.bean.servicetype.response.GetAmountOfAllowedModemSuccessResponse;
import com.example.SmsValidator.entity.ServiceTypeEntity;
import com.example.SmsValidator.repository.ServiceTypeEntityRepository;
import com.example.SmsValidator.utils.ServiceMappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {
    private final ServiceTypeEntityRepository serviceTypeRepository;
    private final TaskService taskService;

    public GetAllowedServiceTypesResponse getActiveServices() {
        List<ServiceTypeEntity> activeServices = serviceTypeRepository.findByActiveTrue();
        return new GetAllowedServiceTypesResponse(activeServices
                .stream()
                .map(ServiceMappingUtils::mapToServiceDto)
                .collect(Collectors.toList()));
    }

    public GetAmountOfAllowedModemSuccessResponse getAmountOfAvailable(String serviceAbbreviation) {
        int amount = taskService.getAmountOfAvailableServices(serviceAbbreviation);
        return GetAmountOfAllowedModemSuccessResponse.builder()
                .amount(amount)
                .service(serviceAbbreviation)
                .build();
    }
}
