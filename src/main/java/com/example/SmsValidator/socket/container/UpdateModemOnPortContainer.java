package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.Data;

@Data
public class UpdateModemOnPortContainer {
    private final Long taskId;
    private final ModemBaseDto modem;
    private final String portName;
}
