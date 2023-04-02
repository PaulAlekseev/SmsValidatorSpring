package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.dto.modem.ModemForProvider;
import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleBlankModemsCommand implements Command {
    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        Type token = new TypeToken<HashMap<String, ModemForProvider>>() {
        }.getType();
        HashMap<String, ModemForProvider> map = gson.fromJson(json, token);
        List<ModemEntity> list = map.values()
                .stream()
                .map(ModemMappingUtils::mapToModemEntity)
                .collect(Collectors.toList());
        List<ModemEntity> handledModems = service.handleBlankModems(list);
        List<ModemForProvider> result = handledModems
                .stream()
                .map((ModemEntity modemEntity) -> ModemMappingUtils
                        .mapToModemForProvider(modemEntity, map.get(modemEntity.getIMSI()).getPort()))
                .collect(Collectors.toList());
        session.sendMessage(MessageFormer.formMessage(OutCommands.SET_STATIONARY_MODEMS, gson.toJson(result)));
    }
}
