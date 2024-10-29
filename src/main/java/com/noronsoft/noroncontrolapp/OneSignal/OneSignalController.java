package com.noronsoft.noroncontrolapp.OneSignal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/notifications")
public class OneSignalController {

    private final OneSignalService oneSignalService;

    public OneSignalController(OneSignalService oneSignalService) {
        this.oneSignalService = oneSignalService;
    }

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam Set<String> playerIds) {
        oneSignalService.sendPushNotification(playerIds, title, message);
        return "Bildirim gönderildi.";
    }
}

