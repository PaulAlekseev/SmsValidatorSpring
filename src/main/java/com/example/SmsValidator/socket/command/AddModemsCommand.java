package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.dto.modem.ModemForProvider;
import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.socket.container.AddModemsOutContainer;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddModemsCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions)
            throws Exception {
        Gson gson = new Gson();

        Type token = new TypeToken<Map<String, ModemForProvider>>() {
        }.getType();
        Map<String, ModemForProvider> modems = gson.fromJson(json, token);
        List<ModemEntity> modemEntityList = modems.values()
                .stream()
                .map(ModemMappingUtils::mapToModemEntity)
                .toList();
        List<ModemEntity> resultModems = service.handleBlankModems(modemEntityList);
        List<String> phoneNumbers = resultModems
                .stream()
                .map(ModemEntity::getPhoneNumber)
                .toList();
        service.connectProviderToModems(phoneNumbers, modemProviderSession);

        AddModemsOutContainer container = AddModemsOutContainer.builder()
                .modems(resultModems
                        .stream()
                        .map((ModemEntity modemEntity) -> ModemMappingUtils
                                .mapToModemForProvider(modemEntity, modems.get(modemEntity.getIMSI()).getPort()))
                        .collect(Collectors.toList()))
                .build();
        session.sendMessage(MessageFormer.formMessage(OutCommands.CONNECT_MODEMS, gson.toJson(container)));
    }
}
