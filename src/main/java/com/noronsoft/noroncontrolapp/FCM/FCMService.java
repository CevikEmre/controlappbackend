package com.noronsoft.noroncontrolapp.FCM;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class FCMService {

    private final RestTemplate restTemplate;
    private final String fcmServerKey;

    public FCMService(@Value("${fcm.server.key}") String fcmServerKey) {
        this.restTemplate = new RestTemplate();
        this.fcmServerKey = fcmServerKey;
    }

    // Birden fazla token'a bildirim gönderme
    public void sendPushNotification(Set<String> deviceTokens, String title, String body) {
        for (String token : deviceTokens) {
            sendSinglePushNotification(token, title, body);
        }
    }


    private void sendSinglePushNotification(String deviceToken, String title, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "key=" + fcmServerKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);

        Map<String, Object> message = new HashMap<>();
        message.put("to", deviceToken);
        message.put("notification", notification);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

        try {
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";
            ResponseEntity<String> response = restTemplate.exchange(fcmUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("FCM bildirimi başarıyla gönderildi: " + deviceToken);
            } else {
                System.err.println("FCM bildirimi gönderilirken hata oluştu: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("FCM bildirimi gönderilirken bir hata oluştu: " + e.getMessage());
        }
    }
}
