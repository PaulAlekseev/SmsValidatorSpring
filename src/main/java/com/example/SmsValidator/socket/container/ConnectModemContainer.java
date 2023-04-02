package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.Data;

@Data
public class ConnectModemContainer {
    private String taskId;
    private ModemBaseDto modem;
}
