package com.example.SmsValidator.utils;

import com.example.SmsValidator.dto.country.CountryBaseDto;
import com.example.SmsValidator.entity.CountryEntity;

public class CountryMappingUtils {
    public static CountryBaseDto mapToCountryBaseDto(CountryEntity country) {
        CountryBaseDto countryBaseDto = new CountryBaseDto();
        countryBaseDto.setId(country.getId());
        countryBaseDto.setCountryCode(country.getCountryCode());
        countryBaseDto.setName(country.getName());
        return countryBaseDto;
    }
}
