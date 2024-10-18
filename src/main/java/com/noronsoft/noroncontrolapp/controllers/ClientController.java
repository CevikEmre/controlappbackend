package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.SaveNewClientResponse;
import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.requestParams.SaveNewClientRequest;
import com.noronsoft.noroncontrolapp.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/checkClient")
    public ResponseEntity<ClientModel> checkClient(@RequestParam String username, @RequestParam String password) {
        return clientService.checkClient(username, password).map(client -> {
            client.setLogin("OK");
            return ResponseEntity.ok(client);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/saveNewClient")
    public ResponseEntity<SaveNewClientResponse> saveNewClient(@Valid @RequestBody SaveNewClientRequest saveNewClientRequest, BindingResult result) {
        SaveNewClientResponse response = new SaveNewClientResponse();

        if (result.hasErrors()) {
            String validationErrors = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            response.setText("ERROR: " + validationErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            clientService.saveClient(saveNewClientRequest);
            response.setUsername("OK");
            response.setText("OK");
            response.setConfirmed("OK");
            response.setPhone("OK");
            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException e) {
            response.setText("ERROR: Duplicate data detected - " + getConstraintViolationMessage(e));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        }
    }


    private String getConstraintViolationMessage(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        if (message.contains("username")) {
            return "Username is already taken";
        } else if (message.contains("email")) {
            return "Email is already in use";
        } else if (message.contains("phone")) {
            return "Phone number is already in use";
        } else {
            return "Duplicate data detected";
        }
    }
}
