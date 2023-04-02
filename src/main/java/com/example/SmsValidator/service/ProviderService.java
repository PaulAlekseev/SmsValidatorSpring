package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.provider.request.ProviderModemDisconnectRequest;
import com.example.SmsValidator.bean.provider.response.ProviderActivateSuccessResponse;
import com.example.SmsValidator.bean.provider.response.ProviderModemsSuccessResponse;
import com.example.SmsValidator.bean.provider.response.ProviderStartAddModemsResponse;
import com.example.SmsValidator.bean.provider.response.ProviderTasksFromProviderIdResponse;
import com.example.SmsValidator.dto.modem.ModemBaseDto;
import com.example.SmsValidator.dto.task.TaskBaseDto;
import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.entity.TaskEntity;
import com.example.SmsValidator.exception.customexceptions.provider.ProviderSessionBusyException;
import com.example.SmsValidator.exception.customexceptions.provider.ProviderSessionNotFoundException;
import com.example.SmsValidator.exception.customexceptions.socket.ModemProviderSessionDoesNotExistException;
import com.example.SmsValidator.repository.ModemProviderSessionEntityRepository;
import com.example.SmsValidator.repository.TaskEntityRepository;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.ModemSocketHandler;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.socket.container.ProviderReadyOutContainer;
import com.example.SmsValidator.socket.container.ProviderStopOutContainer;
import com.example.SmsValidator.socket.container.StartAddModemsContainer;
import com.example.SmsValidator.specification.ModemSpecification;
import com.example.SmsValidator.specification.TaskSpecification;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.example.SmsValidator.utils.TaskMappingUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final Gson gson = new Gson();

    private final ModemService modemService;
    private final ModemProviderSessionEntityRepository modemProviderSessionEntityRepository;
    private final ModemSocketHandler modemSocketHandler;
    private final TaskEntityRepository taskEntityRepository;
    private final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    public ProviderTasksFromProviderIdResponse getTasksFromModemId(Long modemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Specification<TaskEntity> specification = TaskSpecification.hasUser_Email(authentication.getName())
                .and(TaskSpecification.hasModemEntity_Id(modemId));
        List<TaskBaseDto> tasks = taskEntityRepository.findAll(specification)
                .stream()
                .map(TaskMappingUtils::mapToBaseTaskDto)
                .toList();

        return new ProviderTasksFromProviderIdResponse(tasks);
    }

    private String getEmailFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public ProviderActivateSuccessResponse activate()
            throws ProviderSessionNotFoundException, ProviderSessionBusyException, ModemProviderSessionDoesNotExistException, IOException {
        ModemProviderSessionEntity modemProviderSession = getProviderSessionFromContext(true);
        WebSocketSession session = getSocketSessionBySocketId(modemProviderSession.getSocketId());
        session.sendMessage(MessageFormer.formMessage(OutCommands.START_PROVIDER, gson.toJson(new ProviderReadyOutContainer())));
        logger.info(getEmailFromContext() + " starting provider");
        return new ProviderActivateSuccessResponse();
    }

    public ProviderActivateSuccessResponse stop()
            throws ProviderSessionNotFoundException, ProviderSessionBusyException, ModemProviderSessionDoesNotExistException, IOException {
        ModemProviderSessionEntity modemProviderSession = getProviderSessionFromContext(false);
        WebSocketSession session = getSocketSessionBySocketId(modemProviderSession.getSocketId());
        disconnectNotBusyModems(modemProviderSession);
        setProviderBusy(modemProviderSession);
        session.sendMessage(MessageFormer.formMessage(OutCommands.STOP_PROVIDER, gson.toJson(new ProviderStopOutContainer())));
        logger.info(getEmailFromContext() + " stopped provider");
        return new ProviderActivateSuccessResponse();
    }

    public WebSocketSession getSocketSessionBySocketId(String socketId) throws ModemProviderSessionDoesNotExistException {
        WebSocketSession socketSession = modemSocketHandler.getSessions().get(socketId);
        if (socketSession == null)
            throw new ModemProviderSessionDoesNotExistException("Modem provider is not active", this.getClass());
        return socketSession;
    }

    public int setProviderBusy(ModemProviderSessionEntity modemProviderSession) {
        return modemProviderSessionEntityRepository.
                updateBusyBySocketIdAndActiveTrue(true, modemProviderSession.getSocketId());
    }

    public int disconnectNotBusyModems(ModemProviderSessionEntity providerSession) {
        return modemService.disconnectNotBusyModems(providerSession);
    }

    public ProviderActivateSuccessResponse saveModems()
            throws ProviderSessionNotFoundException, ProviderSessionBusyException, ModemProviderSessionDoesNotExistException, IOException {
        ModemProviderSessionEntity modemProviderSession = getProviderSessionFromContext();
        WebSocketSession session = getSocketSessionBySocketId(modemProviderSession.getSocketId());
        session.sendMessage(MessageFormer.formMessage(OutCommands.START_SAVE_MODEMS, gson.toJson(new ProviderReadyOutContainer())));
        logger.info(getEmailFromContext() + " started save modems");
        return new ProviderActivateSuccessResponse();
    }

    private ModemProviderSessionEntity getProviderSessionFromContext()
            throws ProviderSessionNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return modemProviderSessionEntityRepository
                .findByUser_EmailAndActiveTrue(authentication.getName())
                .orElseThrow(() -> new ProviderSessionNotFoundException("Provider session not found", this.getClass()));
    }

    private ModemProviderSessionEntity getProviderSessionFromContext(Boolean requiredBusy)
            throws ProviderSessionNotFoundException, ProviderSessionBusyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModemProviderSessionEntity sessionEntity = modemProviderSessionEntityRepository
                .findByUser_EmailAndActiveTrue(authentication.getName())
                .orElseThrow(() -> new ProviderSessionNotFoundException("Provider session not found", this.getClass()));
        if (!sessionEntity.getBusy() == requiredBusy) {
            String busyAddition = "";
            if (!requiredBusy) busyAddition = "not ";
            throw new ProviderSessionBusyException("Provider is " + busyAddition + "busy", this.getClass());
        }
        return sessionEntity;
    }

    public ProviderModemsSuccessResponse getProvidersWorkingModems() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Specification<ModemEntity> modemSpecification = ModemSpecification.hasModemProviderSession_User_Email(email)
                .and(ModemSpecification.hasModemProviderSessionEntity_Active(true))
                .and(ModemSpecification.hasModemProviderSessionEntity_Busy(false));
        List<ModemBaseDto> data = modemService
                .getModemsWithSpecification(modemSpecification)
                .stream()
                .map(ModemMappingUtils::mapToBaseModemDto)
                .toList();
        return ProviderModemsSuccessResponse.builder()
                .data(data)
                .build();
    }

    public ProviderModemsSuccessResponse getModemsByCriteria(int revenue, String services)
            throws ProviderSessionNotFoundException, ProviderSessionBusyException {
        ModemProviderSessionEntity providerSession = getProviderSessionFromContext(false);
        System.out.println(revenue);
        System.out.println(services);

        Specification<ModemEntity> modemSpecification = ModemSpecification
                .hasBusy(false)
                .and(ModemSpecification.hasModemProviderSessionId(providerSession.getId()))
                .and((modemService.formServiceAbbreviationSpecification(services))
                        .or(ModemSpecification.hasRevenueMoreThan(revenue)));
        List<ModemBaseDto> modems = modemService
                .getModemsWithSpecification(modemSpecification)
                .stream()
                .map(ModemMappingUtils::mapToBaseModemDto)
                .toList();
        return ProviderModemsSuccessResponse.builder()
                .data(modems)
                .build();
    }

    public ProviderModemsSuccessResponse disconnectModems(ProviderModemDisconnectRequest request) throws ProviderSessionNotFoundException, ProviderSessionBusyException, ModemProviderSessionDoesNotExistException, IOException {
        ModemProviderSessionEntity providerSession = getProviderSessionFromContext(false);
        List<String> imsis = request.getModem()
                .stream()
                .map(ModemEntity::getIMSI)
                .toList();
        Specification<ModemEntity> specification = ModemSpecification.hasBusy(false)
                .and(ModemSpecification.hasImsiIn(imsis));
        List<ModemBaseDto> modems = modemService.getModemsWithSpecification(specification)
                .stream()
                .map(ModemMappingUtils::mapToBaseModemDto)
                .toList();

        WebSocketSession socketSession = getSocketSessionBySocketId(providerSession.getSocketId());

        ProviderModemsSuccessResponse response = ProviderModemsSuccessResponse.builder()
                .data(modems)
                .build();
        socketSession.sendMessage(MessageFormer.formMessage(
                OutCommands.DISCONNECT_MODEMS,
                gson.toJson(response)
        ));
        logger.info(getEmailFromContext() + " started disconnect modems");
        return response;
    }

    public ProviderStartAddModemsResponse startAddModems() throws ProviderSessionNotFoundException, ProviderSessionBusyException, ModemProviderSessionDoesNotExistException, IOException {

        ModemProviderSessionEntity modemProviderSession = getProviderSessionFromContext(false);
        WebSocketSession session = getSocketSessionBySocketId(modemProviderSession.getSocketId());
        StartAddModemsContainer container = StartAddModemsContainer.builder()
                .ok(true)
                .build();
        session.sendMessage(MessageFormer.formMessage(OutCommands.ADD_MODEMS, gson.toJson(container)));
        logger.info(getEmailFromContext() + " started add modems");
        return new ProviderStartAddModemsResponse();
    }
}
