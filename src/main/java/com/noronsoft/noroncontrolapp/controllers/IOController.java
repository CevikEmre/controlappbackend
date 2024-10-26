package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.GetIoResponse;
import com.noronsoft.noroncontrolapp.DTOs.SetIOResponse;
import com.noronsoft.noroncontrolapp.requestParams.GetIoParams;
import com.noronsoft.noroncontrolapp.requestParams.SetIoParams;
import com.noronsoft.noroncontrolapp.services.IOService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/io")
public class IOController {

    private final IOService ioService;

    public IOController(IOService ioService) {
        this.ioService = ioService;
    }

    @PostMapping("/setio")
    public ResponseEntity<SetIOResponse> setio(@RequestBody SetIoParams setIoParams, HttpServletRequest httpServletRequest) {
        SetIOResponse response = ioService.setIo(setIoParams, httpServletRequest);

        if ("ERROR".equals(response.getDevice())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // 403 Forbidden
        } else if ("ERROR".equals(response.getConfirmed())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 Internal Server Error
        }

        return ResponseEntity.ok(response);  // 200 OK
    }

    @PostMapping("/getio")
    public ResponseEntity<GetIoResponse> getio(@RequestBody GetIoParams getIoParams, HttpServletRequest httpServletRequest) {
        GetIoResponse response = ioService.getIo(getIoParams, httpServletRequest);

        if ("ERROR".equals(response.getDevice())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // 403 Forbidden
        }

        return ResponseEntity.ok(response);  // 200 OK
    }
}
