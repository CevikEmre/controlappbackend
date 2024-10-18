package com.noronsoft.noroncontrolapp.repositories;

import com.noronsoft.noroncontrolapp.models.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository  extends JpaRepository<MessageModel , Integer> {
    Optional<MessageModel> findByDevIdAndClientId(Integer devId , Integer clientId);
}
