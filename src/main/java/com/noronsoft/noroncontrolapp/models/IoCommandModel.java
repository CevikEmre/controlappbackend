package com.noronsoft.noroncontrolapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "iocommands")
@Getter
@Setter
public class IoCommandModel {

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

}
