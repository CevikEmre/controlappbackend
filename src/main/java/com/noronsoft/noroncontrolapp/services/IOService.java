package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.GetIoResponse;
import com.noronsoft.noroncontrolapp.DTOs.SetIOResponse;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.noronsoft.noroncontrolapp.models.IoCommandModel;
import com.noronsoft.noroncontrolapp.repositories.DeviceRepository;
import com.noronsoft.noroncontrolapp.repositories.IORepository;
import com.noronsoft.noroncontrolapp.requestParams.GetIoParams;
import com.noronsoft.noroncontrolapp.requestParams.SetIoParams;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IOService {

    private final IORepository ioRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;

    @Autowired
    public IOService(IORepository ioRepository, DeviceRepository deviceRepository, DeviceService deviceService) {
        this.ioRepository = ioRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
    }

    public SetIOResponse setIo(SetIoParams setIoParams, HttpServletRequest request) {
        SetIOResponse response = new SetIOResponse();
        Integer userId = (Integer) request.getAttribute("userId");

        Optional<DeviceModel> device = deviceRepository.findById(setIoParams.getDevId());
        if (device.isEmpty() || deviceService.hasAccessToDevice(device.get(), userId)) {
            response.setDevice("ERROR");
            response.setConfirmed("ERROR");
            return response;
        } else {
            response.setDevice("OK");
        }

        // Komut Ekleme
        try {
            IoCommandModel newCommand = new IoCommandModel();
            newCommand.setClientId(userId);
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

    public GetIoResponse getIo(GetIoParams getIoParams, HttpServletRequest request) {
        GetIoResponse response = new GetIoResponse();
        Integer userId = (Integer) request.getAttribute("userId");

        // Cihaz Doğrulama ve Yetki Kontrolü
        Optional<DeviceModel> device = deviceRepository.findById(getIoParams.getDevId());
        if (device.isEmpty() || deviceService.hasAccessToDevice(device.get(), userId)) {
            response.setDevice("ERROR");
            return response;
        } else {
            response.setDevice("OK");
        }

        // Cihazdan Mesajları Çekme
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
