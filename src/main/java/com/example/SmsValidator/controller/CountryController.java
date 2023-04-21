package com.example.SmsValidator.controller;

import com.example.SmsValidator.bean.country.response.GetAllCountriesResponse;
import com.example.SmsValidator.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/country/")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping("getAll")
    public ResponseEntity<GetAllCountriesResponse> getAllCountries(){
        return ResponseEntity.ok(countryService.getAllCountriesResponse());
    }
}
