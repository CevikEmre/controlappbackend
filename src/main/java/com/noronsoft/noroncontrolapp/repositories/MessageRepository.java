package com.noronsoft.noroncontrolapp.repositories;

import com.noronsoft.noroncontrolapp.models.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository  extends JpaRepository<MessageModel , Integer> {
    Optional<MessageModel> findByDevIdAndClientId(Integer devId , Integer clientId);
}
