package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModemMessageOutContainer {
    private long taskId;
    private ModemBaseDto modemState;
    private long timeSeconds;
}
