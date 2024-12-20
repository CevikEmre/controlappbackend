package com.noronsoft.noroncontrolapp.pusher;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        System.out.println("Webhook received payload: " + payload);

        if (payload.containsKey("events")) {
            List<Map<String, Object>> events = (List<Map<String, Object>>) payload.get("events");
            for (Map<String, Object> event : events) {
                String eventName = (String) event.get("name");
                System.out.println("Processing event: " + eventName);

                if ("client_event".equals(eventName)) {
                    try {
                        Map<String, Object> eventData = new ObjectMapper().readValue((String) event.get("data"), Map.class);
                        String error = (String) eventData.get("error");
                        String deviceId = (String) eventData.get("deviceId");
                        System.out.println("Received client-error with deviceId: " + deviceId + ", error: " + error);

                        sendErrorNotification(deviceId, error);
                    } catch (Exception e) {
                        System.err.println("Error parsing event data: " + e.getMessage());
                    }
                } else {
                    System.out.println("Unhandled event name: " + eventName);
                }
            }
        } else {
            System.out.println("No events found in the payload.");
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void sendErrorNotification(String deviceId, String error) {
        System.out.println("Looking up device with ID: " + deviceId);
        Optional<DeviceModel> deviceOptional = deviceRepository.findByDevId(Integer.valueOf(deviceId));

        if (deviceOptional.isPresent()) {
            DeviceModel device = deviceOptional.get();
            System.out.println("Device found: " + device);
            Set<String> deviceTokens = device.getDeviceTokens();

            if (!deviceTokens.isEmpty()) {
                System.out.println("Device tokens: " + deviceTokens);
                String title = "Hata: Cihaz " + deviceId;
                String body = "Cihazınızdan hata alındı: " + error;

                try {
                    fcmService.sendPushNotification(deviceTokens, title, body);
                    System.out.println("Push notification sent successfully.");
                } catch (Exception e) {
                    System.err.println("Error sending push notification: " + e.getMessage());
                }
            } else {
                System.out.println("No device tokens found for deviceId: " + deviceId);
            }
        } else {
            System.out.println("No device found with ID: " + deviceId);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(
            @RequestParam("socket_id") String socketId,
            @RequestParam("channel_name") String channelName) {

        System.out.println("Authenticating socket_id: " + socketId + ", channel_name: " + channelName);
        String auth = pusher.authenticate(socketId, channelName);
        System.out.println("Authentication successful: " + auth);
        return ResponseEntity.ok(auth);
    }
}
