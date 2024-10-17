package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.models.MessageModel;
import com.noronsoft.noroncontrolapp.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Optional<MessageModel> getMessageByDevice(String devId, Integer clientId) {
        return messageRepository.findByDevIdAndClientId(devId, clientId);
    }
}
