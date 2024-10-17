package com.noronsoft.noroncontrolapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "devices")
@Getter
@Setter
public class DeviceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String devId;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean enable;

    //Admin id
    @Column(name = "clientId")
    private Integer clientId;

    //Adminin eklediği user idler
    @Column(name = "otherClientIDs", columnDefinition = "TEXT")
    private List<Integer> otherClientIds;

    @Column(name = "createdDateTime", columnDefinition = "DATETIME")
    private LocalDateTime createdDateTime;

    @Column(name = "activatedDateTime", columnDefinition = "DATETIME")
    private LocalDateTime activatedDateTime;

    private Integer activeDays;
    private BigDecimal yearlyPrice;
    private String m2mNumber;
    private String m2mSerial;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean connected;

    private Integer devType;

    private List<String> device_tokens;
}
