package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.GetIoResponse;
import com.noronsoft.noroncontrolapp.DTOs.SetIOResponse;
import com.noronsoft.noroncontrolapp.requestParams.GetIoParams;
import com.noronsoft.noroncontrolapp.requestParams.SetIoParams;
import com.noronsoft.noroncontrolapp.services.IOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/io")
public class IOController {
    public final IOService ioService;

    public IOController(IOService ioService) {
        this.ioService = ioService;
    }

    @PostMapping("/setio")
    public ResponseEntity<SetIOResponse> setio(@RequestBody SetIoParams setIoParams) {
        SetIOResponse response = ioService.setIo(setIoParams);

        if ("ERROR".equals(response.getLogin())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // 401 Unauthorized
        } else if ("ERROR".equals(response.getDevice())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // 403 Forbidden
        } else if ("ERROR".equals(response.getConfirmed())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 Internal Server Error
        }

        return ResponseEntity.ok(response);  // 200 OK
    }

    @GetMapping("/getio")
    public ResponseEntity<GetIoResponse> getio(@RequestParam GetIoParams getIoParams) {
        GetIoResponse response = ioService.getIo(getIoParams);

        if ("ERROR".equals(response.getLogin())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // 401 Unauthorized
        } else if ("ERROR".equals(response.getDevice())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // 403 Forbidden
        }

        return ResponseEntity.ok(response);  // 200 OK
    }
}
