package com.example.SmsValidator.service.payment;

import com.example.SmsValidator.bean.payment.coinremitter.CoinRemitterEncryptor;
import com.example.SmsValidator.bean.payment.coinremitter.CoinRemitterPaymentProvider;
import com.example.SmsValidator.bean.payment.coinremitter.CustomData;
import com.example.SmsValidator.bean.payment.coinremitter.response.CoinRemitterSiteResponseProvider;
import com.example.SmsValidator.bean.payment.coinremitter.response.CoinRemitterSuccessResponse;
import com.example.SmsValidator.entity.InvoiceEntity;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.payment.NotValidInvoiceException;
import com.example.SmsValidator.exception.customexceptions.payment.UnknownCoinException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.InvoiceEntityRepository;
import com.example.SmsValidator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CoinRemitterPaymentService {

    public static final String PAID_STATUS = "Paid";
    private final CoinRemitterPaymentProvider paymentProvider;
    private final InvoiceEntityRepository invoiceEntityRepository;
    private final UserRepository userRepository;
    private final CoinRemitterEncryptor encryptor;
    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(CoinRemitterPaymentService.class);

    public String sendInvoice(String coin, Float amount, CustomData data) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return restTemplate
                .postForObject(
                        paymentProvider.createInvoiceUrl(coin),
                        paymentProvider.createInvoiceRequest(coin, amount, data),
                        String.class);
    }

    public CoinRemitterSuccessResponse createResponse(String jsonData) throws UnknownCoinException {
        CoinRemitterSiteResponseProvider responseProvider = new CoinRemitterSiteResponseProvider(jsonData);
        CoinRemitterSuccessResponse response = new CoinRemitterSuccessResponse();
        response.setCoin(responseProvider.getCoin());
        response.setUrl(responseProvider.getUrl());
        response.setAddress(responseProvider.getAddress());
        response.setAmountInCoin(responseProvider.getAmount());
        response.setQrCode(responseProvider.createQrCode(paymentProvider));
        response.setAmountInUSD(responseProvider.getAmountUsd());
        return response;
    }

    public CoinRemitterSuccessResponse createInvoice(String coin, Float amount) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Creating invoice for " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
        InvoiceEntity entity = new InvoiceEntity();
        entity.setAmount(amount);
        entity.setUser(user);
        InvoiceEntity resultEntity = invoiceEntityRepository.save(entity);
        CustomData data = CustomData.builder()
                .id(Long.valueOf(resultEntity.getId().toString()))
                .email(user.getEmail())
                .amount(amount)
                .build();

        String remitterResponse = sendInvoice(coin, amount, data);
        CoinRemitterSuccessResponse response = createResponse(remitterResponse);
        logger.info("Successfully created invoice for " + email);
        return response;
    }

    public void processResponse(String status, String customData, String id)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UserNotFoundException, NotValidInvoiceException {
        if ((id != null) && (status != null)) {
            logger.info("Processing invoice " + id + " response with status " + status);
            if (Objects.equals(status, PAID_STATUS)) {
                CustomData data = encryptor.decryptCustomData(customData);
                User user = userRepository
                        .findByEmail(data.getEmail())
                        .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
                paymentService.topUpBalance(data.getAmount(), user, data.getId());
            }
            logger.info("Successfully processed invoice " + id);
        } else {
            logger.info("Ping from coinremitter");
        }
    }
}
