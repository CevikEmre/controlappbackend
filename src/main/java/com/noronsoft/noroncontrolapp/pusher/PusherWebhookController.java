package com.noronsoft.noroncontrolapp.pusher;

import com.noronsoft.noroncontrolapp.FCM.FCMService;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.pusher.rest.Pusher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handlePusherWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook received: " + payload);

        if (payload.containsKey("events")) {
            List<Map<String, Object>> events = (List<Map<String, Object>>) payload.get("events");
            for (Map<String, Object> event : events) {
                String eventName = (String) event.get("name");

                if ("client-error".equals(eventName)) {
                    Map<String, String> eventData = (Map<String, String>) event.get("data");
                    String error = eventData.get("error");
                    String deviceId = eventData.get("deviceId");

                    sendErrorNotification(deviceId, error);
                }
            }
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void sendErrorNotification(String deviceId, String error) {
        Optional<DeviceModel> deviceOptional = deviceRepository.findByDevId(Integer.valueOf(deviceId));

        if (deviceOptional.isPresent()) {
            DeviceModel device = deviceOptional.get();
            Set<String> deviceTokens = device.getDeviceTokens();

            if (!deviceTokens.isEmpty()) {
                String title = "Hata: Cihaz " + deviceId;
                String body = "Cihazınızdan hata alındı: " + error;

                fcmService.sendPushNotification(deviceTokens, title, body);
                System.out.println("Bildirim gönderildi: " + deviceTokens);
            } else {
                System.out.println("Cihaz için kayıtlı token bulunamadı: " + deviceId);
            }
        } else {
            System.out.println("Cihaz bulunamadı: " + deviceId);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(
            @RequestParam("socket_id") String socketId,
            @RequestParam("channel_name") String channelName) {

        String auth = pusher.authenticate(socketId, channelName);
        return ResponseEntity.ok(auth);
    }
}
