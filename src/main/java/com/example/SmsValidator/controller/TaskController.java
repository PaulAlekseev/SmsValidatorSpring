package com.example.SmsValidator.controller;

import com.example.SmsValidator.exception.CustomException;
import com.example.SmsValidator.exception.customexceptions.modem.ModemNotFoundException;
import com.example.SmsValidator.exception.customexceptions.socket.CouldNotFindTaskEntityException;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api/v1/task/")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;


    @PostMapping("create")
    public ResponseEntity<?> createTask(
            Principal principal,
            @RequestParam Long serviceId)
            throws CustomException, IOException {
        return ResponseEntity.ok(taskService.createTask(serviceId, principal));
    }

    @GetMapping("get")
    public ResponseEntity<?> getTask(@RequestParam Long taskId)
            throws CouldNotFindTaskEntityException {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @PostMapping("createReserved")
    public ResponseEntity<?> createReservedTask(Principal principal,
                                                @RequestParam Long serviceId,
                                                @RequestParam Long modemId)
            throws UserNotFoundException, ModemNotFoundException, ServiceNotFoundException, IOException {
        return ResponseEntity.ok(taskService.createReservedTask(serviceId, modemId, principal));
    }
}