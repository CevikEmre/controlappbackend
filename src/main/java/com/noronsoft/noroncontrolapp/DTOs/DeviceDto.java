package com.noronsoft.noroncontrolapp.DTOs;

import com.noronsoft.noroncontrolapp.enums.DeviceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class DeviceDto {
    private Integer id;
    private Integer devId;
    private Boolean enable;
    private Integer clientId;
    private Set<ClientDto> otherClients;
    private LocalDateTime createdDateTime;
    private LocalDateTime activatedDateTime;
    private Integer activeDays;
    private BigDecimal yearlyPrice;
    private String m2mNumber;
    private String m2mSerial;
    private Boolean connected;
    private DeviceType deviceType;
}
