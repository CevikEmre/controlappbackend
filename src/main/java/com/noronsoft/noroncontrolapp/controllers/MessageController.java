package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.GetMessageResponse;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.models.MessageModel;
import com.noronsoft.noroncontrolapp.services.ClientService;
import com.noronsoft.noroncontrolapp.services.DeviceService;
import com.noronsoft.noroncontrolapp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final ClientService clientService;
    private final MessageService messageService;
    private final DeviceService deviceService;

    @Autowired
    public MessageController(ClientService clientService, MessageService messageService, DeviceService deviceService) {
        this.clientService = clientService;
        this.messageService = messageService;
        this.deviceService = deviceService;
    }

    @PostMapping("/getMessage")
    public ResponseEntity<GetMessageResponse> getMessage(@RequestParam String username, @RequestParam String password, @RequestParam String devid, @RequestParam Integer clientid) {
        GetMessageResponse response = new GetMessageResponse();

        Optional<ClientModel> client = clientService.checkClient(username, password);
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            response.setDevice("ERROR");

            return ResponseEntity.badRequest().body(response);
        }

        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("ERROR");

            return ResponseEntity.notFound().build();
        }

        if (!deviceService.hasAccessToDevice(device.get(), clientid)) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            response.setMessage("Access denied");

            return ResponseEntity.badRequest().body(response);
        }
        Optional<MessageModel> message = messageService.getMessageByDevice(devid, clientid);
        if (message.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("OK");
            response.setMessage("No message found");

            return ResponseEntity.badRequest().body(response);
        }

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
