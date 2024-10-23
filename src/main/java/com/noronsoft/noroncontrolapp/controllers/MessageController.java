package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.GetMessageResponse;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.models.MessageModel;
import com.noronsoft.noroncontrolapp.services.DeviceService;
import com.noronsoft.noroncontrolapp.services.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;
    private final DeviceService deviceService;

    @Autowired
    public MessageController(MessageService messageService, DeviceService deviceService) {
        this.messageService = messageService;
        this.deviceService = deviceService;
    }

    @PostMapping("/getMessage")
    public ResponseEntity<GetMessageResponse> getMessage(@RequestParam Integer devid, HttpServletRequest request) {
        GetMessageResponse response = new GetMessageResponse();

        Integer userId = (Integer) request.getAttribute("userId");

        // Cihazı kontrol et
        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            return ResponseEntity.notFound().build();
        }

        if (!deviceService.hasAccessToDevice(device.get(), userId)) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            response.setMessage("Access denied");

            return ResponseEntity.badRequest().body(response);
        }

        // Mesajı getir
        Optional<MessageModel> message = messageService.getMessageByDevice(devid, userId);
        if (message.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("OK");
            response.setMessage("No message found");
            return ResponseEntity.badRequest().body(response);
        }

        // Mesaj bulunduysa yanıtı oluştur
        MessageModel msg = message.get();
        response.setLogin("OK");
        response.setDevice("OK");
        response.setMessage(msg.getCmdText());
        response.setId(msg.getId());
        response.setMsgid(msg.getMsgId());
        response.setNotificationtype(msg.getNotificationType());

        return ResponseEntity.ok(response);
    }
}
