package com.noronsoft.noroncontrolapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> activeSessions() {
        return new ConcurrentHashMap<>();
    }
}
