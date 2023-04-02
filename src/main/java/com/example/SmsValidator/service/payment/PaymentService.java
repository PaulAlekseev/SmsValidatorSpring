package com.example.SmsValidator.service.payment;

import com.example.SmsValidator.entity.InvoiceEntity;
import com.example.SmsValidator.entity.TaskEntity;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.payment.NotValidInvoiceException;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindTaskEntityException;
import com.example.SmsValidator.exception.customexceptions.socket.NotEnoughBalanceException;
import com.example.SmsValidator.repository.InvoiceEntityRepository;
import com.example.SmsValidator.repository.TaskEntityRepository;
import com.example.SmsValidator.repository.UserRepository;
import com.example.SmsValidator.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final InvoiceEntityRepository invoiceEntityRepository;
    private final TaskEntityRepository taskEntityRepository;

    public InvoiceEntity createInvoiceEntity(User user, Float amount) {
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setAmount(amount);
        invoiceEntity.setUser(user);
        return invoiceEntityRepository.save(invoiceEntity);
    }

    public void checkInvoice(Long invoiceId, User user, Float amount) throws NotValidInvoiceException, Error {
        if (invoiceEntityRepository
                .updateValidatedByIdAndValidatedFalseAndUser(true, invoiceId, user) == 0)
            throw new NotValidInvoiceException("Invoice is not valid", this.getClass());
    }

    public boolean topUp(Long userId, float amount) {
        return userRepository.topUpBalance(userId, amount) == 1;
    }

    public void topUpBalance(Float amount, User user, Long invoiceId) throws NotValidInvoiceException, Error {
        checkInvoice(invoiceId, user, amount);
        topUp(user.getId(), amount);
    }

    public boolean returnBalance(Long taskId) throws CouldNotFindTaskEntityException {
        TaskEntity task = taskEntityRepository.findAll(
                        TaskSpecification.hasId(taskId)
                                .and(TaskSpecification.withUserEntity())
                ).stream().findFirst()
                .orElseThrow(() -> new CouldNotFindTaskEntityException("Could not find task", this.getClass()));
        return userRepository
                .topUpBalance(task.getUser().getId(), task.getCost()) > 0;
    }

    public void decreaseBalance(User user, Float amount) throws NotEnoughBalanceException {
        try {
            userRepository.decreaseBalance(user.getId(), amount);
        } catch (Exception e) {
            throw new NotEnoughBalanceException("Not enough balance", this.getClass());
        }
    }
}
