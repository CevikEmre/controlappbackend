package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.ClientDto;
import com.noronsoft.noroncontrolapp.DTOs.DeviceDto;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.noronsoft.noroncontrolapp.requestParams.DeviceAddParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (deviceToken != null && !device.getDeviceTokens().contains(deviceToken)) {
            device.getDeviceTokens().add(deviceToken);
        }

        deviceRepository.save(device);
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

    public List<DeviceDto> getAllDevicesForClient(Integer userId) {
        List<DeviceModel> allDevices = deviceRepository.findAll();

        return allDevices.stream()
                .filter(device ->
                        Optional.ofNullable(device.getClientId()).map(id -> id.equals(userId)).orElse(false) ||
                                Optional.ofNullable(device.getOtherClients())
                                        .map(clients -> clients.stream()
                                                .anyMatch(client -> client.getID().equals(userId)))
                                        .orElse(false)
                )
                .map(device -> convertToDeviceDto(device, userId))
                .collect(Collectors.toList());
    }


    public Optional<DeviceDto> getDeviceDetail(Integer deviceId, Integer userId) {
        Optional<DeviceModel> deviceOptional = checkDevice(deviceId);

        if (deviceOptional.isEmpty() || !hasAccessToDevice(deviceOptional.get(), userId)) {
            return Optional.empty();
        }

        return deviceOptional.map(device -> convertToDeviceDto(device, userId));
    }


    public String addDevice(DeviceAddParams deviceAddParams) throws IllegalArgumentException {
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

        deviceRepository.save(device);

        return "Device with devId " + deviceAddParams.getDevId() + " successfully added.";
    }


    private DeviceDto convertToDeviceDto(DeviceModel device,Integer userId) {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(device.getId());
        deviceDto.setDevId(device.getDevId());
        deviceDto.setEnable(device.getEnable());
        deviceDto.setClientId(device.getClientId());
        deviceDto.setCreatedDateTime(device.getCreatedDateTime());
        deviceDto.setActivatedDateTime(device.getActivatedDateTime());
        deviceDto.setActiveDays(device.getActiveDays());
        deviceDto.setYearlyPrice(device.getYearlyPrice());
        deviceDto.setM2mNumber(device.getM2mNumber());
        deviceDto.setM2mSerial(device.getM2mSerial());
        deviceDto.setConnected(device.getConnected());
        deviceDto.setDeviceType(device.getDeviceType());
        deviceDto.setOtherClients(convertToClientDtos(device.getOtherClients()));
        deviceDto.setIsAdmin(isAdminOfDevice(device, userId));
        return deviceDto;
    }


    public Set<ClientDto> convertToClientDtos(Set<ClientModel> clients) {
        return clients.stream().map(client -> {
            ClientDto clientDto = new ClientDto();
            clientDto.setId(client.getID());
            clientDto.setName(client.getName());
            clientDto.setAddress(client.getAddress());
            clientDto.setCity(client.getCity());
            clientDto.setCountry(client.getCountry());
            clientDto.setEmail(client.getEmail());
            clientDto.setPhone(client.getPhone());
            clientDto.setEnable(client.getEnable());
            return clientDto;
        }).collect(Collectors.toSet());
    }
    public List<String> getDeviceRelays(Integer deviceId) {
        DeviceModel device = deviceRepository.findByDevId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with ID: " + deviceId));
        return device.getRelayNames();
    }

    @Transactional
    public void updateDeviceRelays(Integer deviceId, List<String> relayNames) {
        DeviceModel device = deviceRepository.findByDevId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with ID: " + deviceId));

        // Röle isimlerini güncelle
        device.setRelayNames(relayNames);

        // Veritabanına kaydet
        deviceRepository.save(device);
    }
}
