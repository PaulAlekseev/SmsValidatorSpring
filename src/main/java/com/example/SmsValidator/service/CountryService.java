package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.country.response.GetAllCountriesResponse;
import com.example.SmsValidator.dto.country.CountryBaseDto;
import com.example.SmsValidator.entity.CountryEntity;
import com.example.SmsValidator.repository.CountryEntityRepository;
import com.example.SmsValidator.utils.CountryMappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryEntityRepository countryRepository;

    public List<CountryEntity> getAllCountries() {
        return countryRepository.findAll();
    }

    public Map<String, CountryEntity> getCountriesMap() {
        Map<String, CountryEntity> resultMap = new HashMap<>();
        for (CountryEntity country : getAllCountries()) {
            resultMap.put(country.getImsiCode(), country);
        }
        return resultMap;
    }

    public GetAllCountriesResponse getAllCountriesResponse() {
        List<CountryBaseDto> countries = getAllCountries()
                .stream()
                .map(CountryMappingUtils::mapToCountryBaseDto)
                .toList();
        return new GetAllCountriesResponse(countries);

    }
}
