package com.noronsoft.noroncontrolapp.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    private final ConcurrentHashMap<String, WebSocketSession> activeSessions;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketService(ConcurrentHashMap<String, WebSocketSession> activeSessions) {
        this.activeSessions = activeSessions;
    }

    public boolean sendSetRelayCommand(String devid, String setRelay, String time, String type) {
        WebSocketSession session = activeSessions.get(devid);

        if (session == null) {
            System.out.println("Cihaz bulunamadı: " + devid);
            return false;
        }

        if (!session.isOpen()) {
            System.out.println("Bağlantı kapalı: " + devid);
            return false;
        }

        try {
            Map<String, String> messageMap = Map.of(
                    "setrelay", setRelay,
                    "time", time,
                    "type", type
            );

            String message = objectMapper.writeValueAsString(messageMap);
            session.sendMessage(new TextMessage(message));
            System.out.println("Mesaj gönderildi (" + devid + "): " + message);
            return true;
        } catch (Exception e) {
            System.err.println(String.format("Mesaj gönderilemedi (devid: %s): %s", devid, e.getMessage()));
            e.printStackTrace();
        }
        return false;
    }
}
