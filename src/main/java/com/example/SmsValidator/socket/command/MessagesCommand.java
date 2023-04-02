package com.example.SmsValidator.socket.command;

import com.example.SmsValidator.entity.MessageEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.entity.TaskEntity;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindServiceException;
import com.example.SmsValidator.exception.customexceptions.socket.NoMatchingPatternMessagesException;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.container.MessageContainer;
import com.google.gson.Gson;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class MessagesCommand implements Command {

    @Override
    public void run(String json, ModemProviderSessionEntity modemProviderSession, SocketService service, WebSocketSession session, Map<String, WebSocketSession> sessions) throws Exception {
        Gson gson = new Gson();
        MessageContainer container = gson.fromJson(json, MessageContainer.class);
        TaskEntity task = service.findTaskOnMessage(container.getTaskId(), modemProviderSession);
        MessageEntity message;
        try {
            message = service.filterMessages(task, container.getMessages());
        } catch (NoMatchingPatternMessagesException | CouldNotFindServiceException e) {
            return;
        }
        service.onMatchingMessageTask(task, modemProviderSession, message);
    }
}
