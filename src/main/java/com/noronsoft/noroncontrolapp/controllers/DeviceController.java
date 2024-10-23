package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.requestParams.DeviceAddParams;
import com.noronsoft.noroncontrolapp.services.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/addUserToDevice")
    public ResponseEntity<Void> addUserToDevice(@RequestParam Integer devid, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DeviceModel deviceModel = device.get();

        if (!deviceService.isAdminOfDevice(deviceModel, userId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        deviceService.addUserToDevice(userId, deviceModel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removeUserFromDevice")
    public ResponseEntity<Void> removeUserFromDevice(@RequestParam Integer devid, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DeviceModel deviceModel = device.get();

        if (!deviceService.isAdminOfDevice(deviceModel, userId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        deviceService.removeUserFromDevice(userId, deviceModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllDevicesForClient")
    public ResponseEntity<List<DeviceModel>> getAllDevicesForClient(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<DeviceModel> devices = deviceService.getAllDevicesForClient(userId);

        return ResponseEntity.ok(devices);
    }
    @PostMapping("/addDevice")
    public ResponseEntity<DeviceModel> addDevice(@RequestBody DeviceAddParams deviceAddParams) {
        try {
            DeviceModel device = deviceService.addDevice(deviceAddParams);
            return ResponseEntity.ok(device);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
