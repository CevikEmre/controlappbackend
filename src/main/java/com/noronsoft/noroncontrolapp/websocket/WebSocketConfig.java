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

@Getter
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DeviceWebSocketHandler(), "/chat").setAllowedOrigins("*");
    }

    private class DeviceWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            activeSessions.put(session.getId(), session);
            System.out.println("Yeni cihaz bağlandı: " + session.getId());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            System.out.println("Gelen mesaj (" + session.getId() + "): " + payload);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, String> messageMap = objectMapper.readValue(payload, new TypeReference<>() {});
                if (messageMap.containsKey("serverdevid")) {
                    String serverDevId = messageMap.get("serverdevid");
                    activeSessions.put(serverDevId, session);
                    System.out.println("Cihaz kaydedildi: serverdevid=" + serverDevId);

                    // Geri bir onay mesajı gönder (isteğe bağlı)
                    Map<String, String> response = Map.of("status", "registered", "serverdevid", serverDevId);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                }
                else if (messageMap.containsKey("devid")) {
                    String devid = messageMap.get("devid");
                    activeSessions.put(devid, session);
                    System.out.println("Cihaz kaydedildi: devid=" + devid);
                }

            } catch (Exception e) {
                System.err.println("Mesaj işleme hatası: " + e.getMessage());
            }
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            activeSessions.remove(session.getId());
            System.out.println("Cihaz bağlantısı kapandı: " + session.getId());
        }
    }
}
