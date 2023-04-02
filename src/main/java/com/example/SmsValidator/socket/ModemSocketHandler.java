package com.example.SmsValidator.socket;

import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindSuchModemProviderException;
import com.example.SmsValidator.repository.ModemProviderSessionEntityRepository;
import com.example.SmsValidator.service.SocketService;
import com.example.SmsValidator.socket.handler.SocketMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModemSocketHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ModemSocketHandler.class);

    @Autowired
    private SocketService service;
    @Autowired
    private ModemProviderSessionEntityRepository providerSessionRepo;

    public Map<String, WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ModemProviderSessionEntity providerSession = new ModemProviderSessionEntity();
        providerSession.setSocketId(session.getId());
        providerSession.setActive(true);
        providerSession.setBusy(true);
        Pattern pattern = Pattern.compile("Bearer\s(.*)");
        Matcher matcher = pattern.matcher(session.getHandshakeHeaders().get("authorization").get(0));
        if (!matcher.find()) {
            session.close();
            return;
        }
        String token = matcher.group(1);
        service.createModemProviderSession(providerSession, token);
        sessions.put(session.getId(), session);
        logger.info(session.getId() + " connected to websocket");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message.getPayload().equals("Ping")) return;
        try {
            SocketMessageHandler
                    .handleMessage(
                            message.getPayload().toString(),
                            providerSessionRepo.findBySocketIdAndActiveTrue(session.getId())
                                    .orElseThrow(() -> new CouldNotFindSuchModemProviderException("Could not find such provider", this.getClass())),
                            service,
                            session,
                            getSessions());
        } catch (Exception e) {
            logger.error(session.getId() + e.getLocalizedMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        ModemProviderSessionEntity providerSession = providerSessionRepo.findBySocketIdAndActiveTrue(session.getId())
                .orElseThrow(() -> new CouldNotFindSuchModemProviderException("Could not find such modem provider", this.getClass()));
        service.disconnectModemsFromProvider(providerSession);
        service.deactivateModemProvider(providerSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
