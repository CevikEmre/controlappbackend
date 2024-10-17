package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.AddOrDelClientToDeviceResponse;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<DeviceModel> checkDevice(String devId) {
        return deviceRepository.findByDevId(devId);
    }
    public boolean hasAccessToDevice(DeviceModel device, Integer clientId) {
        if (device.getClientId().equals(clientId)) {
            return true;
        }
        return device.getOtherClientIds().contains(clientId);
    }

    public AddOrDelClientToDeviceResponse addUserToDevice(ClientModel client, DeviceModel device) {
        AddOrDelClientToDeviceResponse response = new AddOrDelClientToDeviceResponse();
        response.setLogin("OK");
        response.setDevice("OK");
        response.setDeleted("NONE");

        if (device.getClientId() == null) {
            device.setClientId(client.getClientId());
            deviceRepository.save(device);
            response.setClientpermission("ADMIN");
            response.setAdded("OK");
            response.setClientperexist("NONE");
        } else if (device.getOtherClientIds().contains(client.getClientId())) {
            response.setClientpermission("USER");
            response.setAdded("NONE");
            response.setClientperexist("YES");
        } else {
            device.getOtherClientIds().add(client.getClientId());
            deviceRepository.save(device);
            response.setClientpermission("USER");
            response.setAdded("OK");
            response.setClientperexist("NONE");
        }

        return response;
    }

    public AddOrDelClientToDeviceResponse removeUserFromDevice(ClientModel client, DeviceModel device) {
        AddOrDelClientToDeviceResponse response = new AddOrDelClientToDeviceResponse();
        response.setLogin("OK");
        response.setDevice("OK");
        response.setAdded("NONE");

        if (client.getClientId().equals(device.getClientId())) {
            response.setClientpermission("ADMIN");
            response.setDeleted("NOTPOSSIBLE");
        } else if (device.getOtherClientIds().contains(client.getClientId())) {
            device.getOtherClientIds().remove(client.getClientId());
            deviceRepository.save(device);
            response.setClientpermission("USER");
            response.setDeleted("YES");
        } else {
            response.setClientpermission("NONE");
            response.setDeleted("NONE");
        }

        return response;
    }
    public List<DeviceModel> getAllDevicesForClient(Integer clientId) {
        List<DeviceModel> adminDevices = deviceRepository.findByClientId(clientId);
        List<DeviceModel> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOtherClientIds().contains(clientId))
                .toList();

        adminDevices.addAll(userDevices);
        return adminDevices;
    }
}

