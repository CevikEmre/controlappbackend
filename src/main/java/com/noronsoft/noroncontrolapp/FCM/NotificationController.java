package com.noronsoft.noroncontrolapp.FCM;

import com.noronsoft.noroncontrolapp.requestParams.NotificationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final FCMService fcmService;

    @Autowired
    public NotificationController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/send")
    public String sendNotification(
           @RequestBody NotificationParam notificationParam) {
        fcmService.sendPushNotification(notificationParam.getDeviceTokens(), notificationParam.getTitle(), notificationParam.getBody());
        return "Bildirim gönderildi";
    }
}
