package com.noronsoft.noroncontrolapp.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceInfoResponse {
    @JsonProperty("getiostat")
    private String getiostat;

    @JsonProperty("inp1")
    private String inp1;

    @JsonProperty("time1")
    private String time1;

    @JsonProperty("inp2")
    private String inp2;

    @JsonProperty("time2")
    private String time2;

    @JsonProperty("inp3")
    private String inp3;

    @JsonProperty("time3")
    private String time3;

    @JsonProperty("relay1")
    private String relay1;

    @JsonProperty("relay2")
    private String relay2;

    @JsonProperty("relay3")
    private String relay3;
}
