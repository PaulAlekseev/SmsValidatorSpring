package com.example.SmsValidator.controller;

import com.example.SmsValidator.bean.authentication.request.AuthenticationRequest;
import com.example.SmsValidator.bean.refreshtoken.request.RefreshTokenBaseRequest;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenBaseRequest request) throws UserNotFoundException {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
