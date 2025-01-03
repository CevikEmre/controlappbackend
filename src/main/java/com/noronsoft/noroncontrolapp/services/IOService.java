package com.noronsoft.noroncontrolapp.services;

import com.pusher.rest.Pusher;
import org.springframework.stereotype.Service;

@Service
public class IOService {

    private final DeviceService deviceService;
    private final Pusher pusher;

    public IOService(DeviceService deviceService, Pusher pusher) {
        this.deviceService = deviceService;
        this.pusher = pusher;
    }

}
