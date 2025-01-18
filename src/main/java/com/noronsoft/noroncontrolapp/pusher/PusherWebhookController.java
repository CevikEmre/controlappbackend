package com.noronsoft.noroncontrolapp.pusher;

import com.noronsoft.noroncontrolapp.FCM.FCMService;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.pusher.rest.Pusher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/pusher")
public class PusherWebhookController {

    private final FCMService fcmService;
    private final DeviceRepository deviceRepository;
    private final Pusher pusher;

    public PusherWebhookController(FCMService fcmService, DeviceRepository deviceRepository, Pusher pusher) {
        this.fcmService = fcmService;
        this.deviceRepository = deviceRepository;
        this.pusher = pusher;
        System.out.println("PusherWebhookController başlatıldı.");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook payload alındı: " + payload);

        if (payload.containsKey("deviceId") && payload.containsKey("text")) {
            try {
                Integer deviceId = (Integer) payload.get("deviceId");
                String text = (String) payload.get("text");

                System.out.println("Webhook tetiklendi [DeviceId: " + deviceId + ", Text: " + text + "]");

                // Gelen veriye göre işlem yapabilirsiniz
                sendErrorNotification(deviceId, text);
            } catch (Exception e) {
                System.err.println("Webhook işlenirken hata: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Payload formatı geçersiz: " + payload);
        }

        return ResponseEntity.ok("Webhook işlendi");
    }

    private void sendErrorNotification(Integer deviceId, String text) {
        System.out.println("Hata bildirimi başlatıldı. Device ID: " + deviceId + ", Mesaj: " + text);

        Optional<DeviceModel> deviceOptional = deviceRepository.findByDevId(deviceId);

        if (deviceOptional.isPresent()) {
            DeviceModel device = deviceOptional.get();
            System.out.println("Cihaz bulundu: " + device);

            Set<String> deviceTokens = device.getDeviceTokens();

            if (!deviceTokens.isEmpty()) {
                String title = "Uyarı: Cihaz " + deviceId;
                String body = "Cihazdan mesaj: " + text;

                try {
                    System.out.println("Push bildirimi gönderiliyor... Tokens: " + deviceTokens);
                    fcmService.sendPushNotification(deviceTokens, title, body);
                    System.out.println("Push bildirimi başarıyla gönderildi.");
                } catch (Exception e) {
                    System.err.println("Push bildirimi gönderilirken hata: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Cihaz için kayıtlı token yok: " + deviceId);
            }
        } else {
            System.out.println("Cihaz bulunamadı: " + deviceId);
        }
    }

    // Gereksiz olan auth endpointi yoruma alınmıştır.
    /*
    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(
            @RequestParam("socket_id") String socketId,
            @RequestParam("channel_name") String channelName) {

        System.out.println("Authenticating socket_id: " + socketId + ", channel_name: " + channelName);
        String auth = pusher.authenticate(socketId, channelName);
        System.out.println("Authentication successful: " + auth);
        return ResponseEntity.ok(auth);
    }
    */
}
