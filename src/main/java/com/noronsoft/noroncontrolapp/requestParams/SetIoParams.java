package com.noronsoft.noroncontrolapp.requestParams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetIoParams {
    private Integer devId;
    private Integer clientId;
    private String message;
}
