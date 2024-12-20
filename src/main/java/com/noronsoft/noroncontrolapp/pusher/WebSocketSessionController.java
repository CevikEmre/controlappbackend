package com.noronsoft.noroncontrolapp.pusher;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ws")
public class WebSocketSessionController {
    private final ConcurrentHashMap<String, String> deviceSessions = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody Map<String, String> payload) {
        String clientDevId = payload.get("serverdevid");

        String serverDevId = UUID.randomUUID().toString();

        deviceSessions.put(clientDevId, serverDevId);

        return ResponseEntity.ok("{\"serverdevid\":\"" + serverDevId + "\"}");
    }

    @GetMapping("/sessions")
    public ResponseEntity<ConcurrentHashMap<String, String>> getSessions() {
        return ResponseEntity.ok(deviceSessions);
    }
}
