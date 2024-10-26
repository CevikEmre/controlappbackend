package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.requestParams.DeviceAddParams;
import com.noronsoft.noroncontrolapp.services.ClientService;
import com.noronsoft.noroncontrolapp.services.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;
    private final ClientService clientService;

    @Autowired
    public DeviceController(DeviceService deviceService, ClientService clientService) {
        this.deviceService = deviceService;
        this.clientService = clientService;
    }

    @PostMapping("/addUserToDevice")
    public ResponseEntity<String> addUserToDevice(
            @RequestParam Integer devid,
            @RequestParam String phone,
            HttpServletRequest request) {

        Integer adminUserId = (Integer) request.getAttribute("userId");
        System.out.println("addUserToDevice called - Device ID: " + devid + ", Phone: " + phone + ", Admin User ID: " + adminUserId);

        Optional<ClientModel> clientOptional = clientService.findByPhone(phone);
        if (clientOptional.isEmpty()) {
            System.out.println("User with provided phone number not found: " + phone);
            return ResponseEntity.badRequest().body("User with provided phone number not found");
        }

        ClientModel client = clientOptional.get();
        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(devid);
        if (deviceOptional.isEmpty()) {
            System.out.println("Device not found with ID: " + devid);
            return ResponseEntity.badRequest().body("Device not found");
        }

        DeviceModel deviceModel = deviceOptional.get();
        String message;

        try {
            deviceService.addUserToDevice(deviceModel, adminUserId, client);
            if (deviceService.isAdminOfDevice(deviceModel, client.getID())) {
                message = "User added as admin";
            } else {
                message = "User added as regular user";
            }
            System.out.println(message);
        } catch (IllegalArgumentException e) {
            System.out.println("Admin rights required to add user to device with ID: " + devid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the admin can add other users to this device");
        }

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/removeUserFromDevice")
    public ResponseEntity<String> removeUserFromDevice(@RequestParam Integer deviceId, @RequestParam String phone, HttpServletRequest request) {
        Integer adminUserId = (Integer) request.getAttribute("userId");
        Optional<ClientModel> clientOptional = clientService.findByPhone(phone);
        if (clientOptional.isEmpty()) {
            System.out.println("User with provided phone number not found: " + phone);
            return ResponseEntity.badRequest().body("User with provided phone number not found");
        }

        Integer userId = clientOptional.get().getID();

        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(deviceId);
        if (deviceOptional.isEmpty()) {
            System.out.println("Device not found with ID: " + deviceId);
            return ResponseEntity.badRequest().body("Device not found");
        }

        DeviceModel deviceModel = deviceOptional.get();

        try {
            deviceService.removeUserFromDevice(deviceModel, adminUserId, userId);
            System.out.println("User removed from device with ID: " + deviceId);
            return ResponseEntity.ok("User removed from device");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to remove user from device. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getAllDevicesForClient")
    public ResponseEntity<List<DeviceModel>> getAllDevicesForClient(@RequestParam String phone) {
        Optional<ClientModel> clientOptional = clientService.findByPhone(phone);
        if (clientOptional.isEmpty()) {
            System.out.println("Client with provided phone number not found: " + phone);
            return ResponseEntity.badRequest().build();
        }

        Integer userId = clientOptional.get().getID();
        List<DeviceModel> devices = deviceService.getAllDevicesForClient(userId);

        System.out.println("Fetched devices for client with ID: " + userId);
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/addDevice")
    public ResponseEntity<DeviceModel> addDevice(@RequestBody DeviceAddParams deviceAddParams) {
        try {
            DeviceModel device = deviceService.addDevice(deviceAddParams);
            System.out.println("Device added with ID: " + device.getDevId());
            return ResponseEntity.ok(device);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add device. Reason: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
