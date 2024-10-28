package com.noronsoft.noroncontrolapp.FCM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final FCMService fcmService;

    @Autowired
    public NotificationController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam Set<String> deviceTokens,
            @RequestParam String title,
            @RequestParam String body) {

        fcmService.sendPushNotification(deviceTokens, title, body);
        return "Bildirim gönderildi";
    }
}
