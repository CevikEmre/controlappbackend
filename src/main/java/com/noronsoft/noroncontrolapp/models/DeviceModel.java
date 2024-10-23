package com.noronsoft.noroncontrolapp.models;

import com.noronsoft.noroncontrolapp.enums.DeviceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices", uniqueConstraints = @UniqueConstraint(columnNames = "devId"))  // Unique constraint eklendi
@Getter
@Setter
public class DeviceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer devId;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean enable;

    @Column(name = "clientId", nullable = false)
    private Integer clientId;

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

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @ElementCollection
    @CollectionTable(name = "device_tokens", joinColumns = @JoinColumn(name = "device_id"))
    @Column(name = "token")
    private List<String> deviceTokens = new ArrayList<>();
}
