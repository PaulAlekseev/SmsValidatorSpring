package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.reservemodem.ReserveModemCostProvider;
import com.example.SmsValidator.bean.reservemodem.response.GetOwnReservedModemSuccessResponse;
import com.example.SmsValidator.bean.reservemodem.response.ReserveModemSuccessResponse;
import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemEntity_;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.modem.ModemNotFoundException;
import com.example.SmsValidator.exception.customexceptions.provider.CouldNotFindSuchModemException;
import com.example.SmsValidator.exception.customexceptions.socket.NotEnoughBalanceException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.ModemEntityRepository;
import com.example.SmsValidator.repository.UserRepository;
import com.example.SmsValidator.service.payment.PaymentService;
import com.example.SmsValidator.specification.ModemSpecification;
import com.example.SmsValidator.specification.extra.Order;
import com.example.SmsValidator.specification.extra.Reserved;
import com.example.SmsValidator.utils.ModemMappingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModemService {

    private final ModemEntityRepository modemEntityRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final ReserveModemCostProvider reserveModemCostProvider;
    private final Logger logger = LoggerFactory.getLogger(ModemService.class);

    public ModemEntity getAvailableForReserveModem()
            throws ModemNotFoundException {
        Specification<ModemEntity> modemSpecification = ModemSpecification
                .withTimesUsedLessThanOrEqual(0)
                .and(ModemSpecification.withTasks())
                .and(ModemSpecification.hasBusy(false))
                .and(ModemSpecification.hasModemProviderSessionEntity_Active(true))
                .and(ModemSpecification.hasModemProviderSessionEntity_Busy(false))
                .and(ModemSpecification.orderBy(Order.DESC, ModemEntity_.ID));
        return modemEntityRepository
                .findAll(modemSpecification)
                .stream().findFirst()
                .orElseThrow(() -> new ModemNotFoundException("Could not find such modem", this.getClass()));
    }

    public ModemEntity getAvailableModem(String serviceAbbreviation)
            throws CouldNotFindSuchModemException {
        return modemEntityRepository.findAll(
                        ModemSpecification.notUsedService(serviceAbbreviation)
                                .and(ModemSpecification.hasModemProviderSessionEntity_Busy(false))
                                .and(ModemSpecification.hasModemProviderSessionEntity_Active(true))
                                .and(ModemSpecification.hasBusy(false))
                                .and(ModemSpecification.isReserved(Reserved.NOT_RESERVED))
                                .and(ModemSpecification.withModemProviderSession())
                ).stream().findFirst()
                .orElseThrow(() -> new CouldNotFindSuchModemException("Could not find such modem", this.getClass()));
    }

    public List<ModemEntity> getModemsWithSpecification(Specification<ModemEntity> specification) {
        return modemEntityRepository
                .findAll(specification);
    }

    public int disconnectNotBusyModems(ModemProviderSessionEntity providerSession) {
        return modemEntityRepository
                .updateModemProviderSessionEntityByModemProviderSessionEntityAndBusyFalse(
                        null, providerSession);
    }

    public Specification<ModemEntity> formServiceAbbreviationSpecification(String specificationAbbreviations) {
        List<String> services = new ArrayList<>(List.of(specificationAbbreviations.split(",")));
        Specification<ModemEntity> specification = ModemSpecification.usedService(services.remove(0));
        for (String service : services) {
            specification = specification.or(ModemSpecification.usedService(service));
        }
        return specification;
    }

    public ReserveModemSuccessResponse reserveModem(String userEmail, int daysToReserve)
            throws ModemNotFoundException, UserNotFoundException, NotEnoughBalanceException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found exception", this.getClass()));
        return reserveModem(user, daysToReserve);
    }

    public ReserveModemSuccessResponse reserveModem(User user, int daysToReserve)
            throws ModemNotFoundException, NotEnoughBalanceException {
        ModemEntity modem = getAvailableForReserveModem();
        paymentService.decreaseBalance(user, reserveModemCostProvider.getCost());
        Date newDate = Date.from((LocalDate.now().plusDays(daysToReserve)).atStartOfDay(ZoneId.systemDefault()).toInstant());
        modem.setReservedUntil(newDate);
        modem.setReservedBy(user);
        logger.info(user.getUsername() + " reserved modem with id " + modem.getId().toString());
        return new ReserveModemSuccessResponse(ModemMappingUtils.mapToModemForUser(modemEntityRepository.save(modem)));
    }

    public GetOwnReservedModemSuccessResponse getReservedModems(User user) {
        return new GetOwnReservedModemSuccessResponse(modemEntityRepository
                .findByReservedBy_IdAndReservedUntilGreaterThanEqualOrderByReservedUntilDesc(user.getId(), new Date())
                .stream()
                .map(ModemMappingUtils::mapToModemForUser)
                .collect(Collectors.toList()));
    }

    public GetOwnReservedModemSuccessResponse getReservedModems(Principal principal) throws UserNotFoundException {
        return getReservedModems(userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass())));
    }

}
