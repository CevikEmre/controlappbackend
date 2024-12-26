package com.noronsoft.noroncontrolapp.requestParams;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NotificationParam {
    private Set<String> deviceTokens;
    private String title;
    private String body;
}
