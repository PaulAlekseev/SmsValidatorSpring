package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class DisconnectModemsInContainer {
    private List<ModemBaseDto> modems;
}
