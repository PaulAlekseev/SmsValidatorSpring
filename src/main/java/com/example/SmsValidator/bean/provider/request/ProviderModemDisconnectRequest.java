package com.example.SmsValidator.bean.provider.request;

import com.example.SmsValidator.entity.ModemEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProviderModemDisconnectRequest {
    private List<ModemEntity> modem;
}
