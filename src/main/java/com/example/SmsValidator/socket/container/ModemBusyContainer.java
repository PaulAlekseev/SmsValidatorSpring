package com.example.SmsValidator.socket.container;

import com.example.SmsValidator.entity.ModemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModemBusyContainer {
    private Long taskId;
    private ModemEntity modem;
}
