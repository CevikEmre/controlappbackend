package com.noronsoft.noroncontrolapp.requestParams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetIoParams {
    private String username;
    private String password;
    private Integer devId;
    private Integer clientId;
}
