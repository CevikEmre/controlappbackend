package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddOrDelClientToDeviceResponse {
    private String login;
    private String device;
    private String clientpermission;
    private String added;
    private String clientperexist;
    private String deleted;
}
