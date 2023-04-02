package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.container.ModemBusyContainer;
import com.google.gson.Gson;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class ModemBusyCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        ModemBusyContainer modemContainer = gson.fromJson(json, ModemBusyContainer.class);
        service.updateModemOnBusyTask(modemContainer.getTaskId(), sessions, modemProviderSession);
    }
}
