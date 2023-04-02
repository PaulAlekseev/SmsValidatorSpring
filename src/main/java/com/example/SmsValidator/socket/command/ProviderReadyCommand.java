package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProviderReadyCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        service.setModemProviderNotBusy(modemProviderSession);
        Type token = new TypeToken<ArrayList<ModemEntity>>() {
        }.getType();
        List<ModemEntity> list = gson.fromJson(json, token);
        List<String> numbers = list
                .stream()
                .map(ModemEntity::getPhoneNumber)
                .toList();
        service.connectProviderToModems(numbers, modemProviderSession);
    }
}