package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.noronsoft.noroncontrolapp.requestParams.DeviceAddParams;
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
        return device.getClientId().equals(userId);
    }

    public void addUserToDevice(Integer userId, DeviceModel device) {
        if (!isAdminOfDevice(device, userId)) {
            throw new IllegalArgumentException("Only the admin (clientId) can add users to this device.");
        }

        if (!device.getOtherClientIds().contains(userId)) {
            device.getOtherClientIds().add(userId);
            deviceRepository.save(device);
        }
    }

    public void removeUserFromDevice(Integer userId, DeviceModel device) {
        if (!isAdminOfDevice(device, userId)) {
            throw new IllegalArgumentException("Only the admin (clientId) can remove users from this device.");
        }

        if (!userId.equals(device.getClientId()) && device.getOtherClientIds().contains(userId)) {
            device.getOtherClientIds().remove(userId);
            deviceRepository.save(device);
        }
    }

    public List<DeviceModel> getAllDevicesForClient(Integer clientId) {
        List<DeviceModel> adminDevices = deviceRepository.findByClientId(clientId);
        List<DeviceModel> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOtherClientIds() != null && device.getOtherClientIds().contains(clientId))
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
        device.setEnable(deviceAddParams.getEnable());
        device.setClientId(deviceAddParams.getClientId());
        device.setActiveDays(deviceAddParams.getActiveDays());
        device.setYearlyPrice(deviceAddParams.getYearlyPrice());
        device.setM2mNumber(deviceAddParams.getM2mNumber());
        device.setM2mSerial(deviceAddParams.getM2mSerial());
        device.setConnected(deviceAddParams.getConnected());
        device.setDeviceType(deviceAddParams.getDeviceType());
        device.setCreatedDateTime(deviceAddParams.getCreatedDateTime());
        device.setActivatedDateTime(deviceAddParams.getActivatedDateTime());

        return deviceRepository.save(device); // Eklenen cihazı geri döndürüyoruz
    }
}
