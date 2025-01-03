package com.noronsoft.noroncontrolapp.controllers;
/*
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/io")
public class IOController {

    private final CustomSocketServer customSocketServer;

    public IOController(CustomSocketServer customSocketServer) {
      this.customSocketServer = customSocketServer;
    }

    @PostMapping("/send-command")
    public ResponseEntity<Map<String, String>> sendCommandToDevice(@RequestParam int deviceId, @RequestParam String message) {
        try {
            customSocketServer.sendMessageToDevice(deviceId, message);
            return ResponseEntity.ok(Map.of("status", "success", "deviceId", String.valueOf(deviceId), "message", message));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}*/