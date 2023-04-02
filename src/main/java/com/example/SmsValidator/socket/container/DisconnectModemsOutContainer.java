package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DisconnectModemsOutContainer {
    private List<ModemBaseDto> data;
}
