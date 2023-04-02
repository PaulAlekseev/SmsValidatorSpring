package com.example.SmsValidator.controller;

import com.example.SmsValidator.bean.reservemodem.request.ReserveModemRequest;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.service.ModemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/modem/")
@RequiredArgsConstructor
public class ModemController {

    private final ModemService modemService;

    @PostMapping(path = "reserve")
    public ResponseEntity<?> reserveModem(Principal principal, @RequestBody ReserveModemRequest request)
            throws Exception {
        return ResponseEntity.ok(modemService.reserveModem(principal.getName(), request.getReserveFor()));
    }

    @GetMapping(path = "getReserved")
    public ResponseEntity<?> getReservedModems(Principal principal) throws UserNotFoundException {
        return ResponseEntity.ok(modemService.getReservedModems(principal));
    }

}
