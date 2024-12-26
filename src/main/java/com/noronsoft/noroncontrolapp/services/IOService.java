package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.DTOs.SetRelay;
import com.noronsoft.noroncontrolapp.models.DeviceModel;
import com.pusher.rest.Pusher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class IOService {

    private final DeviceService deviceService;
    private final Pusher pusher;

    public IOService(DeviceService deviceService, Pusher pusher) {
        this.deviceService = deviceService;
        this.pusher = pusher;
    }

    public ResponseEntity<Map<String, String>> processSendCommand(SetRelay request, HttpServletRequest httpServletRequest) {
        Integer userId = (Integer) httpServletRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token geçersiz veya kullanıcı doğrulanamadı."));
        }

        String setRelay = request.getSetrelay();
        String time = request.getTime();
        String type = request.getType();
        Integer deviceId = request.getDeviceId();

        if (setRelay == null || time == null || type == null || deviceId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Eksik parametreler gönderildi."));
        }

        Optional<DeviceModel> deviceOptional = deviceService.checkDevice(deviceId);
        if (deviceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cihaz bulunamadı."));
        }

        DeviceModel device = deviceOptional.get();

        if (!deviceService.hasAccessToDevice(device, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Cihaza erişim izniniz yok."));
        }

        String channel = "private-device-" + deviceId;

        // Data'yı doğrudan Map olarak oluştur
        Map<String, Object> data = Map.of(
                "setrelay", setRelay,
                "time", time,
                "type", type
        );

        String event = "relay-command";

        // Data'yı doğrudan gönder
        pusher.trigger(channel, event, data);

        return ResponseEntity.ok(Map.of("setrelay", setRelay));
    }

}
