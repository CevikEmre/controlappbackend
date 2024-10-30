package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {
    private Integer id;
    private String name;
    private String address;
    private String city;
    private String country;
    private String email;
    private String phone;
    private Boolean enable;
}
