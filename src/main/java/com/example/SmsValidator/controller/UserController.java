package com.example.SmsValidator.controller;

import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("getInfo")
    public ResponseEntity<?> getBalance()
            throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping("getTaskHistory")
    public ResponseEntity<?> getTaskHistory() {
        return ResponseEntity.ok(userService.getTaskHistory());
    }

    @GetMapping("getActiveTasks")
    public ResponseEntity<?> getActiveTasks() {
        return ResponseEntity.ok(userService.getActiveTaskHistory());
    }
}
