package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.container.ConnectModemContainer;
import com.google.gson.Gson;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class ConnectModemCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        ConnectModemContainer container = gson.fromJson(json, ConnectModemContainer.class);
        ModemBaseDto modem = container.getModem();
        if (container.getModem() == null) return;
        service.connectModemToProvider(
                modemProviderSession,
                modem.getIMSI(),
                modem.getICCID());
    }
}
