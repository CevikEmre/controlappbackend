package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetAllDevicesResponse {
    private String login;
    private List<String> deviceids;
    private List<Integer> deviceserials;
    private List<String> devicepermissions;
}
