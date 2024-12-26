package com.noronsoft.noroncontrolapp.FCM;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

@Service
public class FCMService {

    private final String serviceAccountPath;

    public FCMService(@Value("${firebase.service.account.path}") String serviceAccountPath) {
        this.serviceAccountPath = serviceAccountPath;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("FirebaseApp başarıyla başlatıldı.");
        } catch (IOException e) {
            System.err.println("FirebaseApp başlatılırken hata oluştu: " + e.getMessage());
        }
    }

    public void sendPushNotification(Set<String> deviceTokens, String title, String body) {
        System.out.println("Bildirim gönderimi başlatıldı.");
        System.out.println("Başlık: " + title);
        System.out.println("İçerik: " + body);
        System.out.println("Cihaz Token'ları: " + deviceTokens);

        for (String token : deviceTokens) {
            System.out.println("Token işleniyor: " + token);
            sendSinglePushNotification(token, title, body);
        }

        System.out.println("Tüm token'ler için bildirim gönderimi tamamlandı.");
    }

    private void sendSinglePushNotification(String deviceToken, String title, String body) {
        try {
            System.out.println("Bildirim oluşturuluyor. Token: " + deviceToken);
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            System.out.println("FirebaseMessaging üzerinden gönderim başlatılıyor.");
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM bildirimi başarıyla gönderildi. Yanıt: " + response);
        } catch (Exception e) {
            System.err.println("FCM bildirimi gönderilirken bir hata oluştu. Token: " + deviceToken);
            System.err.println("Hata Mesajı: " + e.getMessage());
        }
    }
}
