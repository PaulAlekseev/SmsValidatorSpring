package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.entity.TaskEntity;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.socket.container.MessageOutContainer;
import com.example.SmsValidator.socket.container.ModemCheckContainer;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.google.gson.Gson;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class ModemCheckCommand implements Command {
    private static final int LOW_SIGNAL_QUALITY = 5;

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        ModemCheckContainer container = gson.fromJson(json, ModemCheckContainer.class);
        if (!service.checkIfModemMatch(container.getTaskId(), container.getModem())) {
            service.disconnectNotBusyModems(modemProviderSession);
            service.checkProvider(container.getPortName(), container.getTaskId(), modemProviderSession, sessions);
            service.updateModemOnBusyTask(container.getTaskId(), sessions, modemProviderSession);
            return;
        }
        if (container.getSignalQuality() < LOW_SIGNAL_QUALITY) {
            service.updateModemOnBusyTask(container.getTaskId(), sessions, modemProviderSession);
            return;
        }
        service.setTaskReady(container.getTaskId());
        TaskEntity task = service.getTaskById(container.getTaskId());
        MessageOutContainer messageContainer = new MessageOutContainer(
                container.getTaskId(),
                task.getTimeSeconds(),
                ModemMappingUtils.mapToBaseModemDto(service.getModemWithIMSIAndICCID(container.getModem()))
        );
        session.sendMessage(
                MessageFormer.formMessage(
                        OutCommands.MESSAGE,
                        gson.toJson(messageContainer)
                ));
    }
}
