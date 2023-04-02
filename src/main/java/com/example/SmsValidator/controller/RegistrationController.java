package com.example.SmsValidator.controller;

import com.example.SmsValidator.bean.authentication.request.RegisterRequest;
import com.example.SmsValidator.bean.authentication.request.RestorePasswordChangeRequest;
import com.example.SmsValidator.bean.authentication.request.RestorePasswordRequest;
import com.example.SmsValidator.exception.customexceptions.user.CouldNotValidateUser;
import com.example.SmsValidator.exception.customexceptions.user.RequestIsOutdatedException;
import com.example.SmsValidator.exception.customexceptions.user.RequestNotValidException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/verify/{key}")
    public ResponseEntity<?> verify(@PathVariable String key)
            throws UserNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, RequestIsOutdatedException, InvalidKeyException, CouldNotValidateUser {
        return ResponseEntity.ok(service.verify(key));
    }

    @PostMapping("/startRestorePassword")
    public ResponseEntity<?> restorePassword(@RequestBody RestorePasswordRequest restorePasswordRequest)
            throws Exception {
        return ResponseEntity.ok(service.startRestorePassword(restorePasswordRequest));
    }

    @PutMapping("/restorePassword/{key}")
    public ResponseEntity<?> restorePasswordFinal(@PathVariable String key, @RequestBody RestorePasswordChangeRequest request)
            throws UserNotFoundException, RequestNotValidException, RequestIsOutdatedException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(service.restorePassword(request, key));
    }
}
