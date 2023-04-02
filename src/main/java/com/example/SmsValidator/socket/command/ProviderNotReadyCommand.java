package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class ProviderNotReadyCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        service.setModemProviderBusy(modemProviderSession);
    }
}
