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
    private final ClientService clientService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, ClientService clientService) {
        this.deviceRepository = deviceRepository;
        this.clientService = clientService;
    }

    public Optional<DeviceModel> checkDevice(Integer devId) {
        return deviceRepository.findByDevId(devId);
    }

    public boolean isAdminOfDevice(DeviceModel device, Integer userId) {
        return device.getClientId() != null && device.getClientId().equals(userId);
    }

    public boolean hasAccessToDevice(DeviceModel device, Integer userId) {
        return isAdminOfDevice(device, userId) || device.getOtherClientIds().contains(userId);
    }

    @Transactional
    public void addUserToDevice(DeviceModel device, Integer adminUserId, ClientModel client) {
        if (device.getClientId() == null) {
            device.setClientId(client.getID());
        } else {
            if (!isAdminOfDevice(device, adminUserId)) {
                throw new IllegalArgumentException("Only the admin (clientId) can add users to this device.");
            }
            device.addOtherClientId(client.getID()); // Helper metodu kullanarak ekleme
        }
        deviceRepository.save(device); // Güncellemeyi kaydet
    }


    @Transactional
    public void removeUserFromDevice(DeviceModel device, Integer adminUserId, Integer userId) {
        if (!isAdminOfDevice(device, adminUserId)) {
            throw new IllegalArgumentException("Only the admin (clientId) can remove users from this device.");
        }

        if (device.getOtherClientIds().contains(userId)) {
            device.getOtherClientIds().remove(userId); // Kullanıcı ID'sini listeden çıkarıyoruz
            deviceRepository.save(device); // Güncellemeleri kaydediyoruz
        }
    }

    public List<DeviceModel> getAllDevicesForClient(Integer clientId) {
        // Kullanıcının admin olduğu cihazları alıyoruz
        List<DeviceModel> adminDevices = deviceRepository.findByClientId(clientId);
        // Kullanıcının ekli olduğu diğer cihazları filtreliyoruz
        List<DeviceModel> userDevices = deviceRepository.findAll().stream()
                .filter(device -> device.getOtherClientIds().contains(clientId))
                .toList();

        adminDevices.addAll(userDevices); // Tüm cihazları birleştiriyoruz
        return adminDevices;
    }

    public DeviceModel addDevice(DeviceAddParams deviceAddParams) throws IllegalArgumentException {
        // Aynı devId'ye sahip bir cihaz varsa hata fırlat
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
