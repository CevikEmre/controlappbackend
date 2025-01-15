package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.ClientDto;
import com.noronsoft.noroncontrolapp.DTOs.DeviceDto;
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
import java.util.Set;

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
        String deviceToken = client.getDeviceToken();

        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(devid);
        if (deviceOptional.isEmpty()) {
            System.out.println("Device not found with ID: " + devid);
            return ResponseEntity.badRequest().body("Device not found");
        }

        DeviceModel deviceModel = deviceOptional.get();
        String message;

        try {
            deviceService.addUserToDevice(deviceModel, adminUserId, client, deviceToken);
            message = deviceService.isAdminOfDevice(deviceModel, client.getID()) ? "User added as admin" : "User added as regular user";
            System.out.println(message);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User is already registered to this device.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists on this device");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the admin can add other users to this device");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user to device: " + e.getMessage());
        }

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/removeUserFromDevice")
    public ResponseEntity<String> removeUserFromDevice(@RequestParam Integer deviceId, @RequestParam String phone, HttpServletRequest request) {
        Integer adminUserId = (Integer) request.getAttribute("userId");
        Optional<ClientModel> clientOptional = clientService.findByPhone(phone);

        if (clientOptional.isEmpty()) {
            System.out.println("User with provided phone number not found: " + phone);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with provided phone number not found");
        }

        Integer userId = clientOptional.get().getID();
        String tokenToRemove = clientOptional.get().getDeviceToken();

        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(deviceId);

        if (deviceOptional.isEmpty()) {
            System.out.println("Device not found with ID: " + deviceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        }

        DeviceModel deviceModel = deviceOptional.get();

        boolean userExists = deviceModel.getOtherClients().stream().anyMatch(client -> client.getID().equals(userId));
        if (!userExists) {
            System.out.println("User with ID " + userId + " not found on device with ID: " + deviceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found on device");
        }

        try {
            deviceService.removeUserFromDevice(deviceModel, adminUserId, userId, tokenToRemove);
            System.out.println("User removed from device with ID: " + deviceId);
            return ResponseEntity.ok("User removed from device");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove user from device: " + e.getMessage());
        }
    }

    @GetMapping("/getAllDevicesForClient")
    public ResponseEntity<List<DeviceDto>> getAllDevicesForClient(HttpServletRequest httpServletRequest) {
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        if (userId == null) {
            System.out.println("User ID not found in token.");
            return ResponseEntity.badRequest().build();
        }

        List<DeviceDto> devices = deviceService.getAllDevicesForClient(userId);
        System.out.println("Fetched devices for client with ID: " + userId);
        return ResponseEntity.ok(devices);
    }
    @GetMapping("/getUsersForDevice")
    public ResponseEntity<?> getUsersForDevice(@RequestParam Integer deviceId,HttpServletRequest httpServletRequest) {
        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(deviceId);
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");

        if (deviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Device with ID " + deviceId + " not found.");
        }

        DeviceModel device = deviceOptional.get();

        if (!deviceService.hasAccessToDevice(device, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have access to this device.");
        }

        Set<ClientDto> otherClients = deviceService.convertToClientDtos(device.getOtherClients());

        return ResponseEntity.ok(otherClients);
    }

    @GetMapping("/getDeviceDetail")
    public ResponseEntity<DeviceDto> getDeviceDetail(@RequestParam Integer deviceId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<DeviceDto> deviceDtoOptional = deviceService.getDeviceDetail(deviceId, userId);

        if (deviceDtoOptional.isEmpty()) {
            System.out.println("Device not found or access denied for device ID: " + deviceId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        System.out.println("Fetched device details for ID: " + deviceId);
        return ResponseEntity.ok(deviceDtoOptional.get());
    }

    @PostMapping("/addDevice")
    public ResponseEntity<String> addDevice(@RequestBody DeviceAddParams deviceAddParams) {
        try {
            String response = deviceService.addDevice(deviceAddParams);
            System.out.println("Device added");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add device. Reason: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{deviceId}/relays")
    public ResponseEntity<List<String>> getDeviceRelays(@PathVariable Integer deviceId) {
        List<String> relays = deviceService.getDeviceRelays(deviceId);
        return ResponseEntity.ok(relays);
    }

    @PutMapping("/{deviceId}/relays")
    public ResponseEntity<String> updateDeviceRelays(@PathVariable Integer deviceId, @RequestBody List<String> relayNames) {
        deviceService.updateDeviceRelays(deviceId, relayNames);

        return ResponseEntity.ok("Relay names updated successfully.");
    }
}
