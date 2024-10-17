package com.noronsoft.noroncontrolapp.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SaveNewClientRequest {
    private String username;
    private String password;
    private String name;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phone;
    private Boolean enable;
    private String deviceToken;
}
