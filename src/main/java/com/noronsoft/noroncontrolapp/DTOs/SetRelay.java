package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetRelay {
    private String setrelay;
    private String time;
    private String type;
    private Integer deviceId;
}
