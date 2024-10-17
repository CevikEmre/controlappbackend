package com.noronsoft.noroncontrolapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class MessageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "devId")
    private Integer devId;

    @Column(name = "cmdText", columnDefinition = "TEXT")
    private String cmdText;

    @Column(name = "createdDateTime", columnDefinition = "DATETIME")
    private LocalDateTime createdDateTime;

    @Column(name = "clientId")
    private Integer clientId;

    @Column(name = "msgId")
    private Integer msgId;

    @Column(name = "notificationType")
    private Integer notificationType;

}

