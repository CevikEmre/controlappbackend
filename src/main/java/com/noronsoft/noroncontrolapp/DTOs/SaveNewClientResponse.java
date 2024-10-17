package com.noronsoft.noroncontrolapp.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveNewClientResponse {
    private String username;
    private String text;
    private String confirmed;
    private String phone;
}
