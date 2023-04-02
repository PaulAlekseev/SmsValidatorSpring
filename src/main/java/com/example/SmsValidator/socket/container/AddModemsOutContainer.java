package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemForProvider;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddModemsOutContainer {
    private List<ModemForProvider> modems;
}
