package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.SetRelay;
import com.noronsoft.noroncontrolapp.services.IOService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/io")
public class IOController {

    private final IOService ioService;

    public IOController(IOService ioService) {
        this.ioService = ioService;
    }

    @PostMapping("/send-command")
    public ResponseEntity<Map<String, String>> sendCommandToDevice(
            @RequestBody SetRelay request,
            HttpServletRequest httpServletRequest) {

        try {
            return ioService.processSendCommand(request, httpServletRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Komut gönderilirken bir hata oluştu.", "details", e.getMessage()));
        }
    }
}
