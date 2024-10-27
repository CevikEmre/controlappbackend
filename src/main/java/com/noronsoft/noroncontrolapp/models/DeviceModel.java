package com.noronsoft.noroncontrolapp.models;

import com.noronsoft.noroncontrolapp.enums.DeviceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "devices", uniqueConstraints = @UniqueConstraint(columnNames = "devId"))
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

    @Column(name = "clientId")
    private Integer clientId;

    @ManyToMany
    @JoinTable(
            name = "device_other_clients",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<ClientModel> otherClients = new HashSet<>();

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
    private Set<String> deviceTokens = new HashSet<>();

    // Diğer client eklemek için yöntem
    public void addOtherClient(ClientModel client) {
        otherClients.add(client);
    }

    // Token eklemek için yöntem
    public void addDeviceToken(String deviceToken) {
        deviceTokens.add(deviceToken);
    }
}