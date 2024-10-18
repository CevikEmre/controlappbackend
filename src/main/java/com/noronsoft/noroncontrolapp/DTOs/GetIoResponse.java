package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetIoResponse {
    private String login;
    private String device;
    private String message;
    private String id;
}
