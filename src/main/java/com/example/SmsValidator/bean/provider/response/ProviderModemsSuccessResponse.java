package com.example.SmsValidator.bean.provider.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderModemsSuccessResponse extends BaseResponse {
    private List<ModemBaseDto> data;
}
