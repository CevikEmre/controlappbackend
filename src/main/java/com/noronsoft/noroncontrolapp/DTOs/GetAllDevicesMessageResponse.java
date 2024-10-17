package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


// EKLENECEK
@Getter
@Setter
public class GetAllDevicesMessageResponse {
    private String login;
    private List<String> deviceserials;
    private List<String> messages;
    private List<String> ids;
    private List<String> msgids;
    private List<String> notificationtypes;
    private String device;
}
