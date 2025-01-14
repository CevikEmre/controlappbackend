package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.GetDeviceInfo;
import com.noronsoft.noroncontrolapp.DTOs.SetRelay;
import com.noronsoft.noroncontrolapp.services.IOService;
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
    public ResponseEntity<Map<String, String>> sendCommandToDevice(@RequestBody SetRelay setRelay) {
        try {
            // IOService ile mesajı gönder
            String response = ioService.sendMessageToDevice(setRelay);

            // Başarılı yanıt döndür
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "deviceId", String.valueOf(setRelay.getDeviceId()),
                    "response", response
            ));
        } catch (Exception e) {
            // Hata durumunda 500 döndür
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/getDeviceInfo")
    public ResponseEntity<Map<String, String>> getDeviceInfo(@RequestBody GetDeviceInfo getDeviceInfo) {
        try {
            // IOService ile mesajı gönder
            String response = ioService.getDeviceInfo(getDeviceInfo);

            // Başarılı yanıt döndür
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "deviceId", String.valueOf(getDeviceInfo.getDeviceId()),
                    "response", response
            ));
        } catch (Exception e) {
            // Hata durumunda 500 döndür
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
