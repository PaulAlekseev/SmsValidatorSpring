package com.example.SmsValidator.bean.reservemodem.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.modem.ModemForUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetOwnReservedModemSuccessResponse extends BaseResponse {
    private List<ModemForUserDto> reservedModems;
}
