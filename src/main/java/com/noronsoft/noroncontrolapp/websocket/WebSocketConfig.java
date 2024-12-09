package com.noronsoft.noroncontrolapp.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ConcurrentHashMap<String, WebSocketSession> activeSessions;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketConfig(ConcurrentHashMap<String, WebSocketSession> activeSessions) {
        this.activeSessions = activeSessions;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DeviceWebSocketHandler(), "/chat").setAllowedOrigins("*");

        scheduler.scheduleAtFixedRate(() -> {
            activeSessions.forEach((key, session) -> {
                try {
                    if (session.isOpen()) {
                        Map<String, String> pingMessage = Map.of("echodev", "1");
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pingMessage)));
                    }
                } catch (Exception e) {
                    System.err.println("Ping mesajı gönderilemedi: " + e.getMessage());
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }

    private class DeviceWebSocketHandler extends TextWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            // Initially, store the session with session ID
            activeSessions.put(session.getId(), session);
            System.out.println("New device connected: " + session.getId());

            // Send initial connection message
            Map<String, String> response = Map.of("status", "connected", "message", "Connection successful");
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            System.out.println("Received message (" + session.getId() + "): " + payload);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, String> messageMap = objectMapper.readValue(payload, new TypeReference<>() {});
                if (messageMap.containsKey("serverdevid")) {
                    String serverDevId = messageMap.get("serverdevid");
                    // Remove the session with session ID key
                    activeSessions.remove(session.getId());
                    // Add the session with serverDevId as key
                    activeSessions.put(serverDevId, session);
                    System.out.println("Device registered: serverdevid=" + serverDevId);

                    Map<String, String> response = Map.of("status", "registered", "serverdevid", serverDevId);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                } else if (messageMap.containsKey("ClientID")) {
                    String clientId = messageMap.get("ClientID");
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                            Map.of("status", "success", "message", "ClientID received: " + clientId)
                    )));
                } else {
                    throw new IllegalArgumentException("Unknown message format");
                }
            } catch (Exception e) {
                System.err.println("Message processing error: " + e.getMessage());
                session.sendMessage(new TextMessage("{\"status\":\"error\",\"message\":\"Invalid message format\"}"));
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            activeSessions.remove(session.getId());
            System.out.println("Cihaz bağlantısı kapandı: " + session.getId());
        }
    }
}
