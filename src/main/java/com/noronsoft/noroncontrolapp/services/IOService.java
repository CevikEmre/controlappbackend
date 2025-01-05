package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.SetRelay;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IOService {

    private final WebClient webClient;

    public IOService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://185.186.25.186/server")
                .build();
    }

    public String sendMessageToDevice(SetRelay setRelay) {
        return this.webClient.post()
                .uri("/setRelay.php")
                .bodyValue(setRelay)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
