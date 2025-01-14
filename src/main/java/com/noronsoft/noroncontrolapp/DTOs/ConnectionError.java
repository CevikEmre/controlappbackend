package com.noronsoft.noroncontrolapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionError {
    private String status;
    private String message;
    private String deviceId;
}

