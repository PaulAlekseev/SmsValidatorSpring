package com.example.SmsValidator.bean.reservemodem.response;

import com.example.SmsValidator.bean.BaseResponse;
import com.example.SmsValidator.dto.modem.ModemForUserDto;
import lombok.Data;

@Data
public class ReserveModemSuccessResponse extends BaseResponse {
    private ModemForUserDto modem;

    public ReserveModemSuccessResponse(ModemForUserDto modem) {
        this.modem = modem;
    }
}
