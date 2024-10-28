package com.noronsoft.noroncontrolapp.websocket;

import com.noronsoft.noroncontrolapp.FCM.FCMService;
import com.noronsoft.noroncontrolapp.FCM.NotificationModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ChatController {
    private final FCMService fcmService;
    private final DeviceRepository deviceRepository;

    @Autowired
    public ChatController(FCMService fcmService, DeviceRepository deviceRepository) {
        this.fcmService = fcmService;
        this.deviceRepository = deviceRepository;
    }

    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public NotificationModel processMessage(NotificationModel notificationModel) {
        try {
            Integer devId = notificationModel.getDevId();
            Optional<DeviceModel> deviceOptional = deviceRepository.findByDevId(devId);

            if (deviceOptional.isPresent()) {
                DeviceModel device = deviceOptional.get();
                fcmService.sendPushNotification(device.getDeviceTokens(), notificationModel.getTitle(), notificationModel.getBody());
                notificationModel.setStatus("Bildirim gönderildi");
            } else {
                notificationModel.setStatus("Cihaz bulunamadı: devId " + devId);
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            notificationModel.setStatus("Bildirim gönderilirken hata oluştu: " + e.getMessage());
        }
        return notificationModel;
    }
}
