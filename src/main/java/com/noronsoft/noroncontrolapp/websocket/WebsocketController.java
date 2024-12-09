package com.noronsoft.noroncontrolapp.websocket;

import com.noronsoft.noroncontrolapp.requestParams.SetRelayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ws")
public class WebsocketController {

    private final WebSocketService webSocketService;

    public WebsocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @PostMapping("/setrelay")
    public ResponseEntity<String> setRelay(
            @RequestParam String devid,
            @RequestBody SetRelayRequest request) {

        boolean success = webSocketService.sendSetRelayCommand(devid, request.getSetRelay(), request.getTime(), request.getType());

        if (success) {
            return ResponseEntity.ok("SetRelay komutu gönderildi: " + request);
        } else {
            return ResponseEntity.badRequest().body("SetRelay komutu gönderilemedi. Cihaz bağlantısı yok.");
        }
    }
}
