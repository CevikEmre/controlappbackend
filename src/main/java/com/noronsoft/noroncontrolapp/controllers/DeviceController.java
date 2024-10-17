package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.AddOrDelClientToDeviceResponse;
import com.noronsoft.noroncontrolapp.DTOs.DeviceCheckResponse;
import com.noronsoft.noroncontrolapp.DTOs.GetAllDevicesResponse;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.services.ClientService;
import com.noronsoft.noroncontrolapp.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final ClientService clientService;
    private final DeviceService deviceService;

    @Autowired
    public DeviceController(ClientService clientService, DeviceService deviceService) {
        this.clientService = clientService;
        this.deviceService = deviceService;
    }

    @PostMapping("/checkDevice")
    public ResponseEntity<DeviceCheckResponse> checkDevice(@RequestParam Integer devid, @RequestParam String username, @RequestParam String password) {
        DeviceCheckResponse response = new DeviceCheckResponse();

        Optional<ClientModel> client = clientService.checkClient(username, password);
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            response.setDevice("ERROR");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            return ResponseEntity.ok(response);
        }

        DeviceModel dev = device.get();
        response.setLogin("OK");
        response.setDevice("OK");
        response.setClientid(dev.getClientId());
        response.setCreateddatetime(dev.getCreatedDateTime().toString());
        response.setActivateddatetime(dev.getActivatedDateTime().toString());
        response.setOtherclientids(dev.getOtherClientIds());
        response.setActivedays(dev.getActiveDays());
        response.setYearlyprice(dev.getYearlyPrice().toString());
        response.setM2mserial(dev.getM2mSerial());
        response.setM2mnumber(dev.getM2mNumber());
        response.setConnected(dev.getConnected());
        response.setEnable(dev.getEnable());
        response.setDevtype(String.valueOf(dev.getDevType()));

        return ResponseEntity.ok(response);
    }
    @PostMapping("/addUserToDevice")
    public ResponseEntity<AddOrDelClientToDeviceResponse> addUserToDevice(@RequestParam Integer devid, @RequestParam String username, @RequestParam String password) {
        AddOrDelClientToDeviceResponse response = new AddOrDelClientToDeviceResponse();

        // Kullanıcı doğrulaması
        Optional<ClientModel> client = clientService.checkClient(username, password);
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            return ResponseEntity.ok(response);
        }

        // Cihaz doğrulaması
        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            return ResponseEntity.ok(response);
        }


        DeviceModel deviceModel = device.get();
        AddOrDelClientToDeviceResponse clientPermission = deviceService.addUserToDevice(client.get(), deviceModel);

        response.setLogin("OK");
        response.setDevice("OK");
        response.setClientpermission(clientPermission.getClientpermission());
        response.setAdded("OK");
        response.setDeleted("NONE");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/removeUserFromDevice")
    public ResponseEntity<AddOrDelClientToDeviceResponse> removeUserFromDevice(@RequestParam Integer devid, @RequestParam String username, @RequestParam String password) {
        AddOrDelClientToDeviceResponse response = new AddOrDelClientToDeviceResponse();

        // Kullanıcı doğrulaması
        Optional<ClientModel> client = clientService.checkClient(username, password);
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            return ResponseEntity.ok(response);
        }

        Optional<DeviceModel> device = deviceService.checkDevice(devid);
        if (device.isEmpty()) {
            response.setLogin("OK");
            response.setDevice("ERROR");
            return ResponseEntity.ok(response);
        }

        DeviceModel deviceModel = device.get();
        AddOrDelClientToDeviceResponse deletionStatus = deviceService.removeUserFromDevice(client.get(), deviceModel);

        response.setLogin("OK");
        response.setDevice("OK");
        response.setClientpermission("USER");
        response.setAdded("NONE");
        response.setDeleted(deletionStatus.getDeleted());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getAllDevicesForClient")
    public ResponseEntity<GetAllDevicesResponse> getAllDevicesForClient(@RequestParam String username, @RequestParam String password) {
        GetAllDevicesResponse response = new GetAllDevicesResponse();

        // Kullanıcı doğrulaması
        Optional<ClientModel> client = clientService.checkClient(username, password);
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            return ResponseEntity.ok(response);
        }

        ClientModel clientModel = client.get();
        response.setLogin("OK");

        // Kullanıcıya bağlı tüm cihazları al
        List<DeviceModel> devices = deviceService.getAllDevicesForClient(clientModel.getID());

        // Yanıt verilerini doldur
        List<String> deviceIds = new ArrayList<>();
        List<Integer> deviceSerials = new ArrayList<>();
        List<String> devicePermissions = new ArrayList<>();

        for (DeviceModel device : devices) {
            deviceIds.add(device.getId().toString());
            deviceSerials.add(device.getDevId());
            if (device.getClientId().equals(clientModel.getID())) {
                devicePermissions.add("ADMIN");
            } else {
                devicePermissions.add("USER");
            }
        }

        response.setDeviceids(deviceIds);
        response.setDeviceserials(deviceSerials);
        response.setDevicepermissions(devicePermissions);

        return ResponseEntity.ok(response);
    }
}

