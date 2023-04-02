package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageOutContainer {
    private Long taskId;
    private Long timeSeconds;
    private ModemBaseDto modem;
}
