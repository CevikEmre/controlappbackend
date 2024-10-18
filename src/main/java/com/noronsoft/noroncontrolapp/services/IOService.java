package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.GetIoResponse;
import com.noronsoft.noroncontrolapp.DTOs.SetIOResponse;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.models.IoCommandModel;
import com.noronsoft.noroncontrolapp.repositories.ClientRepository;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.noronsoft.noroncontrolapp.repositories.IORepository;
import com.noronsoft.noroncontrolapp.requestParams.GetIoParams;
import com.noronsoft.noroncontrolapp.requestParams.SetIoParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IOService {

    private final IORepository ioRepository;
    private final ClientRepository clientRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public IOService(IORepository ioRepository, ClientRepository clientRepository, DeviceRepository deviceRepository) {
        this.ioRepository = ioRepository;
        this.clientRepository = clientRepository;
        this.deviceRepository = deviceRepository;
    }

    public SetIOResponse setIo(SetIoParams setIoParams) {
        SetIOResponse response = new SetIOResponse();

        // 1. Kullanıcı Doğrulama
        Optional<ClientModel> client = clientRepository.findByUsernameAndPassword(setIoParams.getUsername(), setIoParams.getPassword());
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            response.setDevice("ERROR");
            response.setConfirmed("ERROR");
            return response;
        } else {
            response.setLogin("OK");
        }

        // 2. Cihaz Doğrulama ve Yetki Kontrolü
        Optional<DeviceModel> device = deviceRepository.findById(setIoParams.getDevId());
        if (device.isEmpty()) {
            response.setDevice("ERROR");
            response.setConfirmed("ERROR");
            return response;
        }

        DeviceModel deviceModel = device.get();

        // Kullanıcının cihaz üzerinde yetkisini kontrol et
        if (!deviceModel.getClientId().equals(setIoParams.getClientId()) &&
                !deviceModel.getOtherClientIds().contains(setIoParams.getClientId())) {
            response.setDevice("ERROR");
            response.setConfirmed("ERROR");
            return response;
        } else {
            response.setDevice("OK");
        }

        // Komut Ekleme
        try {
            IoCommandModel newCommand = new IoCommandModel();
            newCommand.setClientId(setIoParams.getClientId());
            newCommand.setDevId(setIoParams.getDevId());
            newCommand.setCmdText(setIoParams.getMessage());
            newCommand.setCreatedDateTime(LocalDateTime.now());
            ioRepository.save(newCommand);
            response.setConfirmed("OK");
        } catch (Exception e) {
            response.setConfirmed("ERROR");
        }

        return response;
    }

    public GetIoResponse getIo(GetIoParams getIoParams) {
        GetIoResponse response = new GetIoResponse();

        // 1. Kullanıcı Doğrulama
        Optional<ClientModel> client = clientRepository.findByUsernameAndPassword(getIoParams.getUsername(), getIoParams.getPassword());
        if (client.isEmpty()) {
            response.setLogin("ERROR");
            response.setDevice("ERROR");
            return response;
        } else {
            response.setLogin("OK");
        }

        // 2. Cihaz Doğrulama ve Yetki Kontrolü
        Optional<DeviceModel> device = deviceRepository.findById(getIoParams.getDevId());
        if (device.isEmpty()) {
            response.setDevice("ERROR");
            return response;
        }

        DeviceModel deviceModel = device.get();

        // Kullanıcının cihaz üzerinde yetkisini kontrol et
        if (!deviceModel.getClientId().equals(getIoParams.getClientId()) &&
                !deviceModel.getOtherClientIds().contains(getIoParams.getClientId())) {
            response.setDevice("ERROR");
            return response;
        } else {
            response.setDevice("OK");
        }

        // 3. Cihazdan Mesajları Çekme
        Optional<IoCommandModel> ioCommand = ioRepository.findLastestByDevId(getIoParams.getDevId());
        if (ioCommand.isPresent()) {
            response.setMessage(ioCommand.get().getCmdText());
            response.setId(ioCommand.get().getId().toString());
        } else {
            response.setMessage("No Data");
            response.setId("0");
        }

        return response;
    }
}
