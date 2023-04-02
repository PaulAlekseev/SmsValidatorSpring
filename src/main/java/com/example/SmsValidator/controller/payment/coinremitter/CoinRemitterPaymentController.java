package com.example.SmsValidator.controller.payment.coinremitter;

import com.example.SmsValidator.exception.customexceptions.payment.NotValidInvoiceException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.payment.CoinRemitterPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(path = "/api/v1/payment/coinRemitter/")
@RequiredArgsConstructor
public class CoinRemitterPaymentController {
    private final CoinRemitterPaymentService coinRemitterPaymentService;

    @PostMapping(path = "createInvoice")
    public ResponseEntity<?> createInvoice(@RequestParam String coin, @RequestParam Float amount)
            throws Exception {
        return ResponseEntity.ok(coinRemitterPaymentService.createInvoice(coin, amount));
    }

    @PostMapping(path = "notify", consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<?> notify(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) String custom_data1,
                                    @RequestParam(required = false) String id)
            throws UserNotFoundException, NotValidInvoiceException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        coinRemitterPaymentService.processResponse(status, custom_data1, id);
        return ResponseEntity.ok("OK");
    }
}
