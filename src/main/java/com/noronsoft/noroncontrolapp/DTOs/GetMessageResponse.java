package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter public class GetMessageResponse {
    private String login;
    private String device;
    private String message;
    private Integer id;
    private Integer msgid;
    private Integer notificationtype;
}
