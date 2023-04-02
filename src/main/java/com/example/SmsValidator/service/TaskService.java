package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.task.response.TaskSuccessResponse;
import com.example.SmsValidator.config.SocketConfiguration;
import com.example.SmsValidator.entity.*;
import com.example.SmsValidator.exception.CustomException;
import com.example.SmsValidator.exception.customexceptions.modem.ModemNotFoundException;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindServiceException;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindTaskEntityException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.*;
import com.example.SmsValidator.service.payment.PaymentService;
import com.example.SmsValidator.socket.MessageFormer;
import com.example.SmsValidator.socket.OutCommands;
import com.example.SmsValidator.socket.container.ModemCheckOutContainer;
import com.example.SmsValidator.specification.ModemSpecification;
import com.example.SmsValidator.specification.TaskSpecification;
import com.example.SmsValidator.specification.extra.Reserved;
import com.example.SmsValidator.utils.ModemMappingUtils;
import com.example.SmsValidator.utils.TaskMappingUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskEntityRepository taskRepo;
    private final ModemService modemService;
    private final ModemEntityRepository modemEntityRepository;
    private final SocketConfiguration socketConfiguration;
    private final ModemProviderSessionEntityRepository providerSessionRepository;
    private final ServiceTypeEntityRepository serviceRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public boolean checkIfReserved(Long taskId) {
        return taskRepo.existsByIdAndReservedTrue(taskId);
    }

    public int getAmountOfAvailableServices(String serviceAbbreviation) {
        return (int) modemEntityRepository.count(
                ModemSpecification.notUsedService(serviceAbbreviation)
                        .and(ModemSpecification.hasModemProviderSessionEntity_Busy(false))
                        .and(ModemSpecification.hasModemProviderSessionEntity_Active(true))
                        .and(ModemSpecification.hasBusy(false))
                        .and(ModemSpecification.isReserved(Reserved.NOT_RESERVED))
        );
    }

    public TaskSuccessResponse createReservedTask(Long serviceId, Long modemId, Principal principal) throws ModemNotFoundException, UserNotFoundException, ServiceNotFoundException, IOException {
        ModemEntity modem = modemEntityRepository.findByIdAndBusyFalse(modemId)
                .orElseThrow(() -> new ModemNotFoundException("Could not found such modem", this.getClass()));
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
        return createReservedTask(serviceId, modem, user);
    }

    public TaskSuccessResponse createReservedTask(Long serviceId, ModemEntity chosenModem, User user)
            throws ServiceNotFoundException, IOException {
        TaskEntity task = new TaskEntity();
        ServiceTypeEntity serviceType = serviceRepository
                .findById(serviceId)
                .orElseThrow(ServiceNotFoundException::new);
        task.setServiceTypeEntity(serviceType);
        ModemProviderSessionEntity chosenProvider = providerSessionRepository.findByModemEntityList_Id(chosenModem.getId());
        task.setModemEntity(chosenModem);
        task.setModemProviderSessionEntity(chosenProvider);
        task.setTimeSeconds(serviceType.getTimeSeconds());
        task.setUser(user);
        task.setServiceName(serviceType.getAbbreviation());
        task.setCost(0F);
        task.setReserved(true);
        WebSocketSession chosenSession = socketConfiguration.
                getSocketHandler().
                getSessions().
                get(chosenProvider.getSocketId());
        TaskEntity resultTask = taskRepo.save(task);
        resultTask.setMessages(new ArrayList<>());
        ModemCheckOutContainer container = new ModemCheckOutContainer(
                resultTask.getId(),
                ModemMappingUtils.mapToBaseModemDto(chosenModem)
        );
        Gson gson = new Gson();
        chosenSession.sendMessage(MessageFormer.formMessage(OutCommands.CHECK_MODEM, gson.toJson(container)));
        modemEntityRepository.
                updateBusyByPhoneNumberAndModemProviderSessionEntity(
                        true, chosenModem.getPhoneNumber(), chosenProvider);
        logger.info("Started task " + task.getId());
        return new TaskSuccessResponse(
                ModemMappingUtils.mapToModemForUser(chosenModem),
                TaskMappingUtils.mapToTaskForUserDto(resultTask, resultTask.getModemEntity().getPhoneNumber())
        );
    }

    public TaskSuccessResponse createTask(Long serviceId, Principal principal)
            throws CustomException, IOException {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return createTask(serviceId, user);
    }

    public TaskSuccessResponse createTask(Long serviceId, User user)
            throws CustomException, IOException {
        TaskEntity task = new TaskEntity();
        ServiceTypeEntity serviceType = serviceRepository
                .findById(serviceId)
                .orElseThrow(() -> new CouldNotFindServiceException("Could not find such Service", this.getClass()));
        task.setServiceTypeEntity(serviceType);
        ModemEntity chosenModem = modemService.getAvailableModem(serviceType.getAbbreviation());
        ModemProviderSessionEntity chosenProvider = providerSessionRepository.findByModemEntityList_Id(chosenModem.getId());
        task.setModemEntity(chosenModem);
        task.setModemProviderSessionEntity(chosenProvider);
        task.setTimeSeconds(serviceType.getTimeSeconds());
        task.setUser(user);
        task.setReserved(false);
        task.setServiceName(serviceType.getAbbreviation());
        task.setCost(serviceType.getCost());
        WebSocketSession chosenSession = socketConfiguration.
                getSocketHandler().
                getSessions().
                get(chosenProvider.getSocketId());
        paymentService.decreaseBalance(user, serviceType.getCost());
        TaskEntity resultTask = taskRepo.save(task);
        resultTask.setMessages(new ArrayList<>());

        ModemCheckOutContainer container = new ModemCheckOutContainer(
                resultTask.getId(),
                ModemMappingUtils.mapToBaseModemDto(chosenModem)
        );
        Gson gson = new Gson();
        chosenSession.sendMessage(MessageFormer.formMessage(OutCommands.CHECK_MODEM, gson.toJson(container)));
        modemEntityRepository.
                updateBusyByPhoneNumberAndModemProviderSessionEntity(
                        true, chosenModem.getPhoneNumber(), chosenProvider);
        logger.info("Started task " + task.getId());
        return new TaskSuccessResponse(
                ModemMappingUtils.mapToModemForUser(chosenModem),
                TaskMappingUtils.mapToTaskForUserDto(
                        task,
                        task.getModemEntity().getPhoneNumber()
                )
        );
    }

    public TaskSuccessResponse getTask(Long taskId)
            throws CouldNotFindTaskEntityException {
        Specification<TaskEntity> modemSpecification = TaskSpecification
                .hasId(taskId)
                .and(TaskSpecification.withModemEntity());
        TaskEntity taskEntity = taskRepo.findAll(modemSpecification)
                .stream().findFirst()
                .orElseThrow(() -> new CouldNotFindTaskEntityException("TaskForUserDto does not exist", this.getClass()));
        ModemEntity modemEntity = taskEntity.getModemEntity();
        return new TaskSuccessResponse(
                ModemMappingUtils.mapToModemForUser(modemEntity),
                TaskMappingUtils.mapToTaskForUserDto(taskEntity, taskEntity.getModemEntity().getPhoneNumber())
        );
    }
}
