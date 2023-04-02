package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.entity.ModemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModemCheckContainer {
    private Long taskId;
    private int signalQuality;
    private ModemEntity modem;
    private String portName;

}
