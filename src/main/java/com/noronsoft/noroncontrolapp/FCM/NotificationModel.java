package com.noronsoft.noroncontrolapp.FCM;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationModel {
    private Integer devId;
    private String title;
    private String body;
    private String status;
}
