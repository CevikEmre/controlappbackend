package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.noronsoft.noroncontrolapp.requestParams.DeviceAddParams;
import jakarta.transaction.Transactional;
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

    public Optional<DeviceModel> checkDevice(Integer devId) {
        return deviceRepository.findByDevId(devId);
    }

    public boolean isAdminOfDevice(DeviceModel device, Integer userId) {
        return device.getClientId() != null && device.getClientId().equals(userId);
    }

    public boolean hasAccessToDevice(DeviceModel device, Integer userId) {
        // device.getOtherClients() çağrısı ile erişim kontrolü
        return isAdminOfDevice(device, userId) || device.getOtherClients().stream()
                .anyMatch(client -> client.getID().equals(userId));
    }

    @Transactional
    public void addUserToDevice(DeviceModel device, Integer adminUserId, ClientModel client, String deviceToken) {
        if (device.getClientId() == null) {
            device.setClientId(client.getID());
        } else {
            if (!isAdminOfDevice(device, adminUserId)) {
                throw new IllegalArgumentException("Only the admin (clientId) can add users to this device.");
            }
            if (device.getOtherClients().contains(client)) {
                throw new IllegalArgumentException("User is already registered to this device.");
            }
            device.getOtherClients().add(client);
        }

        // deviceToken ekleme işlemi
        if (deviceToken != null && !device.getDeviceTokens().contains(deviceToken)) {
            device.addDeviceToken(deviceToken);
        }

        deviceRepository.save(device);

        // Doğrulama işlemi: Kullanıcı ve token gerçekten eklendi mi?
        if (!device.getOtherClients().contains(client) || (deviceToken != null && !device.getDeviceTokens().contains(deviceToken))) {
            throw new IllegalStateException("User or token could not be added to the device.");
        }
    }


    @Transactional
    public void removeUserFromDevice(DeviceModel device, Integer adminUserId, Integer userId, String tokenToRemove) {
        if (!isAdminOfDevice(device, adminUserId)) {
            throw new IllegalArgumentException("Only the admin (clientId) can remove users from this device.");
        }

        if (adminUserId.equals(userId)) {
            throw new IllegalArgumentException("Admin user cannot be removed from the device.");
        }

        device.getOtherClients().removeIf(client -> client.getID().equals(userId));
        device.getDeviceTokens().remove(tokenToRemove);

        deviceRepository.save(device);


        boolean userStillExists = device.getOtherClients().stream().anyMatch(client -> client.getID().equals(userId));
        boolean tokenStillExists = device.getDeviceTokens().contains(tokenToRemove);

        if (userStillExists || tokenStillExists) {
            throw new IllegalStateException("User or token could not be removed from the device.");
        }
    }

    public List<DeviceModel> getAllDevicesForClient(Integer clientId) {
        List<DeviceModel> adminDevices = deviceRepository.findByClientId(clientId);
        List<DeviceModel> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOtherClients().stream()
                        .anyMatch(client -> client.getID().equals(clientId)))
                .toList();

        adminDevices.addAll(userDevices);

        return adminDevices;
    }

    public DeviceModel addDevice(DeviceAddParams deviceAddParams) throws IllegalArgumentException {
        Optional<DeviceModel> existingDevice = deviceRepository.findByDevId(deviceAddParams.getDevId());
        if (existingDevice.isPresent()) {
            throw new IllegalArgumentException("Device with devId " + deviceAddParams.getDevId() + " already exists.");
        }

        DeviceModel device = new DeviceModel();
        device.setDevId(deviceAddParams.getDevId());
        device.setActiveDays(deviceAddParams.getActiveDays());
        device.setYearlyPrice(deviceAddParams.getYearlyPrice());
        device.setM2mNumber(deviceAddParams.getM2mNumber());
        device.setM2mSerial(deviceAddParams.getM2mSerial());
        device.setDeviceType(deviceAddParams.getDeviceType());
        device.setCreatedDateTime(deviceAddParams.getCreatedDateTime());

        return deviceRepository.save(device);
    }
}
