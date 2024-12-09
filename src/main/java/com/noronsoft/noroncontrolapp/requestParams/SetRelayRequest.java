package com.noronsoft.noroncontrolapp.requestParams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetRelayRequest {
    private String setRelay;
    private String time;
    private String type;
}
