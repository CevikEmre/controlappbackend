package com.noronsoft.noroncontrolapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noronsoft.noroncontrolapp.DTOs.GetDeviceInfo;
import com.noronsoft.noroncontrolapp.DTOs.SetRelay;
import com.noronsoft.noroncontrolapp.services.IOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getDeviceInfo(@RequestBody GetDeviceInfo getDeviceInfo) {
        try {
            String rawResponse = ioService.getDeviceInfo(getDeviceInfo);
            System.out.println("Raw Response: [" + rawResponse + "]");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> jsonResponse = objectMapper.readValue(rawResponse, new TypeReference<>() {});

            return ResponseEntity.ok(jsonResponse);

        } catch (JsonProcessingException e) {
            System.out.println("JSON processing failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Invalid JSON format",
                    "details", e.getMessage()
            ));
        } catch (Exception e) {
            System.out.println("General error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
