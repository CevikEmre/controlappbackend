package com.noronsoft.noroncontrolapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices")
@Getter
@Setter
public class DeviceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer devId;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean enable;

    // Admin id
    @Column(name = "clientId")
    private Integer clientId;

    // Adminin eklediği user idler
    @ElementCollection
    @CollectionTable(name = "device_other_clients", joinColumns = @JoinColumn(name = "device_id"))
    @Column(name = "otherClientId")
    private List<Integer> otherClientIds = new ArrayList<>();

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

    @ElementCollection
    @CollectionTable(name = "device_tokens", joinColumns = @JoinColumn(name = "device_id"))
    @Column(name = "token")
    private List<String> deviceTokens = new ArrayList<>();
}

