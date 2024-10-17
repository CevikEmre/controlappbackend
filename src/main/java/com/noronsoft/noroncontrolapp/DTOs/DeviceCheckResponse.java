package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeviceCheckResponse {
    private String login;
    private String device;
    private Integer clientid;
    private String createddatetime;
    private String activateddatetime;
    private List<Integer> otherclientids;
    private Integer activedays;
    private String yearlyprice;
    private String m2mserial;
    private String m2mnumber;
    private Boolean connected;
    private Boolean enable;
    private String devtype;
}
