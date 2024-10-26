package com.noronsoft.noroncontrolapp.requestParams;

import com.noronsoft.noroncontrolapp.enums.DeviceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Getter
    @Setter
    public class DeviceAddParams {
        private Integer devId;
        private Integer activeDays;
        private BigDecimal yearlyPrice;
        private String m2mNumber;
        private String m2mSerial;
        private DeviceType deviceType;
        private LocalDateTime createdDateTime;
    }
