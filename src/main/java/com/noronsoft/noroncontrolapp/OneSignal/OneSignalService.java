package com.noronsoft.noroncontrolapp.OneSignal;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Set;

@Service
public class OneSignalService {

    private final String appId;
    private final String apiKey;

    public OneSignalService(
            @Value("${onesignal.app.id}") String appId,
            @Value("${onesignal.api.key}") String apiKey) {
        this.appId = appId;
        this.apiKey = apiKey;
    }

    public void sendPushNotification(Set<String> playerIds, String title, String message) {
        for (String playerId : playerIds) {
            sendSinglePushNotification(playerId, title, message);
        }
    }

    private void sendSinglePushNotification(String playerId, String title, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json; charset=UTF-8");
            headers.set("Authorization", "Basic " + apiKey);

            JSONObject body = new JSONObject();
            body.put("app_id", appId);
            body.put("include_player_ids", new String[]{playerId});
            body.put("headings", new JSONObject().put("en", title));
            body.put("contents", new JSONObject().put("en", message));

            HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();

            String apiUrl = "https://onesignal.com/api/v1/notifications";
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            System.out.println("OneSignal bildirimi başarıyla gönderildi: " + response.getBody());
        } catch (Exception e) {
            System.err.println("OneSignal bildirimi gönderilirken hata oluştu: " + e.getMessage());
        }
    }
}
