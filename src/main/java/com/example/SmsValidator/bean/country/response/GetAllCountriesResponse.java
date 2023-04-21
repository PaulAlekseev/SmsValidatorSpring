package com.example.SmsValidator.bean.country.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.country.CountryBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetAllCountriesResponse extends BaseResponse {
    private List<CountryBaseDto> countries;
}
