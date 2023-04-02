package com.example.SmsValidator.service;

import com.example.SmsValidator.auth.JwtTokenProvider;
import com.example.SmsValidator.dto.modem.ModemBaseDto;
import com.example.SmsValidator.entity.*;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindServiceException;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindTaskEntityException;
import com.example.SmsValidator.exception.customexceptions.socket.ModemProviderSessionAlreadyActiveException;
import com.example.SmsValidator.exception.customexceptions.socket.NoMatchingPatternMessagesException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.*;
import com.example.SmsValidator.service.payment.PaymentService;
import com.example.SmsValidator.socket.CountryFind;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.socket.container.CheckProviderOutContainer;
import com.example.SmsValidator.socket.container.ModemCheckOutContainer;
import com.example.SmsValidator.specification.TaskSpecification;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocketService {

    private final ModemProviderSessionEntityRepository modemProviderSessionRepo;
    private final ModemEntityRepository modemEntityRepository;
    private final ModemService modemService;
    private final TaskEntityRepository taskEntityRepository;
    private final TaskService taskService;
    private final MessageEntityRepository messageEntityRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PaymentService paymentService;

    public TaskEntity getTaskById(Long id) {
        return taskEntityRepository.findById(id).get();
    }

    public void updateModemOnSuccess(TaskEntity task) {
        ModemEntity modem = task.getModemEntity();
        modem.addService(task.getServiceName());
        modemEntityRepository.save(modem);
    }

    public int setTaskDone(Long taskId, ModemProviderSessionEntity providerSession)
            throws CouldNotFindTaskEntityException {
        TaskEntity task = taskEntityRepository
                .findAll(
                        TaskSpecification.withModemEntity()
                                .and(TaskSpecification.hasId(taskId))
                                .and(TaskSpecification.withServiceType())
                                .and(TaskSpecification.hasProviderSession_Id(providerSession.getId()))
                ).stream().findFirst()
                .orElse(null);
        if (task == null) {
            return 0;
        }
        if (!task.getSuccess()) {
            paymentService.returnBalance(taskId);
        }
        return setTaskDone(task, providerSession);
    }

    public int disconnectModemsFromProvider(List<ModemBaseDto> modems, ModemProviderSessionEntity modemProviderSession) {
        List<String> imsis = modems.stream()
                .map(ModemBaseDto::getIMSI)
                .toList();
        return modemEntityRepository
                .updateModemProviderSessionEntityByModemProviderSessionEntityAndIMSIIn(
                        null,
                        modemProviderSession,
                        imsis
                );
    }

    public int disconnectModemOnBusyProvider(long taskId) {
        ModemEntity modemEntity = modemEntityRepository
                .findByTaskEntity_IdAndModemProviderSessionEntity_BusyTrue(taskId);
        if (modemEntity == null) {
            return 0;
        }
        return modemEntityRepository.updateModemProviderSessionEntityById(null, modemEntity.getId());
    }

    private int setTaskDone(TaskEntity task, ModemProviderSessionEntity providerSession) {
        if (task.getDone()) return 0;
        return taskEntityRepository.
                updateDoneByIdAndModemProviderSessionEntity(true, task.getId(), providerSession);
    }

    public int setModemProviderBusy(ModemProviderSessionEntity modemProviderSession) {
        return modemProviderSessionRepo.
                updateBusyBySocketIdAndActiveTrue(true, modemProviderSession.getSocketId());
    }

    public int setModemsNotBusy(List<ModemEntity> modems) {
        return modemEntityRepository.
                updateBusyByPhoneNumberIn(
                        false,
                        modems.stream().map(ModemEntity::getPhoneNumber).collect(Collectors.toList()));
    }

    public int setModemProviderNotBusy(ModemProviderSessionEntity modemProviderSession) {
        return modemProviderSessionRepo.
                updateBusyBySocketIdAndActiveTrue(false, modemProviderSession.getSocketId());
    }

    public ModemProviderSessionEntity createModemProviderSession(ModemProviderSessionEntity modemProviderSessionEntity, String token)
            throws Exception {
        User user = userRepository.findByEmail(tokenProvider.getUserName(token))
                .orElseThrow(() -> new UserNotFoundException("Username not found", this.getClass()));
        modemProviderSessionEntity.setUser(user);
        if (!modemProviderSessionRepo.
                existsBySocketIdAndActiveTrue(modemProviderSessionEntity.getSocketId())) {
            return modemProviderSessionRepo.save(modemProviderSessionEntity);
        }
        throw new ModemProviderSessionAlreadyActiveException("Modem provider already active", this.getClass());
    }

    public int deactivateModemProvider(ModemProviderSessionEntity modemProviderSession) {
        modemEntityRepository.updateModemProviderSessionEntityByModemProviderSessionEntityNotNull(null);
        return modemProviderSessionRepo.
                updateActiveBySocketIdAndActiveTrue(false, modemProviderSession.getSocketId());
    }

    public List<ModemEntity> filterModems(List<ModemEntity> modems, List<ModemEntity> modemsFromDb) {
        Map<String, ModemEntity> phoneModems = new HashMap<>();
        for (ModemEntity modem : modemsFromDb) {
            phoneModems.put(modem.getIMSI(), modem);
        }
        List<ModemEntity> result = new ArrayList<>();
        for (ModemEntity modem : modems) {
            ModemEntity modemEntity = phoneModems.get(modem.getIMSI());
            if (modemEntity == null) continue;
            if (Objects.equals(modemEntity.getICCID(), modem.getICCID())) {
                result.add(modemEntity);
            }
        }
        return result;
    }

    public int connectProviderToModems(List<String> phoneNumbers, ModemProviderSessionEntity modemProvider) {
        return modemEntityRepository
                .updateModemProviderSessionEntityAndBusyByPhoneNumberInAndModemProviderSessionEntityNull(
                        modemProvider,
                        false,
                        phoneNumbers
                );
    }

    public int connectModemToProvider(ModemProviderSessionEntity providerSession, String imsi, String iccid) {
        return modemEntityRepository
                .updateModemProviderSessionEntityAndBusyByIMSIAndICCID(
                        providerSession,
                        false,
                        imsi,
                        iccid
                );
    }

    public int disconnectModemsFromProvider(ModemProviderSessionEntity providerSession) {
        return modemEntityRepository.
                updateModemProviderSessionEntityByModemProviderSessionEntity(providerSession, null);
    }

    public int disconnectModemFromProvider(ModemEntity modem, ModemProviderSessionEntity providerSession) {
        return modemEntityRepository.
                updateModemProviderSessionEntityByModemProviderSessionEntityAndIMSI(
                        null,
                        providerSession,
                        modem.getIMSI());
    }

    public List<ModemEntity> saveNewModems(List<ModemEntity> modems) {
        List<String> existingNumbers = modemEntityRepository.findByPhoneNumberIn(modems
                        .stream()
                        .map(ModemEntity::getPhoneNumber)
                        .collect(Collectors.toList())
                )
                .stream()
                .map(ModemEntity::getPhoneNumber)
                .toList();
        List<ModemEntity> resultModems = modems
                .stream()
                .filter(m -> !existingNumbers.contains(m.getPhoneNumber()))
                .toList();
        for (ModemEntity modem : resultModems) {
            CountryFind.Country country = CountryFind.findCountry(modem.getIMSI().substring(0, 3));
            modem.setCountry(country.getCountryName());
            modem.setCountryCode(country.getCountryCode());
        }
        return (List<ModemEntity>) modemEntityRepository.saveAll(resultModems);
    }

    public ModemEntity getModemWithIMSIAndICCID(ModemEntity modem) {
        if (modem.getIMSI() == null || modem.getICCID() == null) return null;
        return modemEntityRepository.findByIMSIAndICCID(modem.getIMSI(), modem.getICCID());
    }

    public int setModemBusy(ModemEntity modem, ModemProviderSessionEntity modemProviderSession) {
        return modemEntityRepository.
                updateBusyByPhoneNumberAndModemProviderSessionEntity(
                        true, modem.getPhoneNumber(), modemProviderSession);
    }

    public int setModemNotBusy(ModemEntity modem, ModemProviderSessionEntity modemProviderSession) {
        return modemEntityRepository.
                updateBusyByPhoneNumberAndModemProviderSessionEntity(
                        false, modem.getPhoneNumber(), modemProviderSession);
    }

    public ModemEntity updateModemOnBusyTask(Long taskId, Map<String, WebSocketSession> sessions, ModemProviderSessionEntity providerSession)
            throws CouldNotFindTaskEntityException, IOException {
        if (taskService.checkIfReserved(taskId)) return null;
        ModemEntity chosenModem = null;
        try {
            chosenModem = modemService.getAvailableModem(
                    taskEntityRepository.findById(taskId)
                            .orElseThrow(() -> new CouldNotFindServiceException("Could not find such service", this.getClass()))
                            .getServiceTypeEntity().getAbbreviation()
            );
        } catch (Exception e) {
            setTaskNotReady(taskId);
            setTaskDone(taskId, providerSession);
            return null;
        }
        ModemProviderSessionEntity newModemProvider = modemProviderSessionRepo.
                findByModemEntityList_Id(chosenModem.getId());
        taskEntityRepository.
                updateModemEntityAndModemProviderSessionEntityById(
                        chosenModem,
                        newModemProvider,
                        taskId);
        WebSocketSession newModemProviderSession = sessions.get(newModemProvider.getSocketId());
        Gson gson = new Gson();
        ModemCheckOutContainer modemCheckContainer = new ModemCheckOutContainer(
                taskId,
                ModemMappingUtils.mapToBaseModemDto(chosenModem)
        );
        newModemProviderSession.sendMessage(
                MessageFormer.formMessage(OutCommands.CHECK_MODEM, gson.toJson(modemCheckContainer)));
        return chosenModem;
    }

    public TaskEntity findTaskOnMessage(Long taskId, ModemProviderSessionEntity providerSession) throws Exception {
        return taskEntityRepository
                .findAll(TaskSpecification.hasId(taskId)
                        .and(TaskSpecification.hasProviderSession_Id(providerSession.getId()))
                        .and(TaskSpecification.withModemEntity())
                        .and(TaskSpecification.withServiceType()))
                .stream().findFirst()
                .orElseThrow(() -> new CouldNotFindTaskEntityException("Could not find task", this.getClass()));
    }

    public MessageEntity filterMessages(
            TaskEntity task,
            List<MessageEntity> messages)
            throws CouldNotFindServiceException, NoMatchingPatternMessagesException {
        ServiceTypeEntity service = task.getServiceTypeEntity();
        if (service == null)
            throw new CouldNotFindServiceException("Could not find service", this.getClass());
        Pattern messageRegex = Pattern.compile(service.getMessageRegex());
        Pattern senderRegex = Pattern.compile(service.getSenderRegex());
        for (MessageEntity message : messages) {
            Matcher messageMatcher = messageRegex.matcher(message.getMessage());
            if (!messageMatcher.find()) continue;
            Matcher senderMatcher = senderRegex.matcher(message.getSender());
            if (!senderMatcher.find()) continue;
            return message;
        }
        throw new NoMatchingPatternMessagesException("Could not find matching message", this.getClass());
    }

    public void onMatchingMessageTask(TaskEntity task, ModemProviderSessionEntity providerSession, MessageEntity message) {
        message.setTaskEntity(task);
        setTaskSuccess(task.getId());
        updateModemOnSuccess(task);
        messageEntityRepository.save(message);
    }

    public int setTaskSuccess(Long taskId) {
        return taskEntityRepository.updateSuccessAndDoneById(true, true, taskId);
    }


    public List<ModemEntity> handleBlankModems(List<ModemEntity> blankModems) {
        List<ModemEntity> modems = modemEntityRepository.
                findByIMSIIn(blankModems.stream().map(ModemEntity::getIMSI).collect(Collectors.toList()));
        Map<String, ModemEntity> imsiModems = new HashMap<>();
        for (ModemEntity modem : modems) {
            imsiModems.put(modem.getIMSI(), modem);
        }
        List<ModemEntity> result = new ArrayList<>();
        for (ModemEntity modem : blankModems) {
            ModemEntity trueModem = imsiModems.get(modem.getIMSI());
            if (trueModem == null) {
                continue;
            }
            if (Objects.equals(trueModem.getICCID(), modem.getICCID())) {
                result.add(trueModem);
            }
        }
        return result;
    }

    public boolean checkIfModemMatch(Long taskId, ModemEntity modem) {
        if (modem == null) return false;
        return taskEntityRepository.
                existsByIdAndModemEntity_IMSIAndModemEntity_ICCID(taskId, modem.getIMSI(), modem.getICCID());
    }

    public int setTaskReady(Long taskId) {
        return taskEntityRepository.updateReadyById(true, taskId);
    }

    public int setTaskNotReady(Long taskId) {
        return taskEntityRepository.updateReadyById(false, taskId);
    }

    public int disconnectNotBusyModems(ModemProviderSessionEntity providerSession) {
        return modemEntityRepository
                .updateModemProviderSessionEntityByModemProviderSessionEntityAndBusyFalse(
                        null, providerSession);
    }

    public void checkProvider(String nameCausePort, Long taskId, ModemProviderSessionEntity providerSession, WebSocketSession socketSession) throws IOException {
        modemProviderSessionRepo.
                updateBusyByActiveTrueAndSocketId(true, providerSession.getSocketId());
        Gson gson = new Gson();
        CheckProviderOutContainer container = new CheckProviderOutContainer(taskId, nameCausePort);
        socketSession.sendMessage(MessageFormer.formMessage(
                OutCommands.UPDATE_PROVIDER, gson.toJson(container)
        ));
    }

    public void checkProvider(String nameCausePort, Long taskId, ModemProviderSessionEntity providerSession, Map<String, WebSocketSession> sessions) throws IOException {
        WebSocketSession session = sessions.get(providerSession.getSocketId());
        checkProvider(nameCausePort, taskId, providerSession, session);
    }
}
