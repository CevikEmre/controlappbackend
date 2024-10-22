package com.noronsoft.noroncontrolapp.controllers;

import com.noronsoft.noroncontrolapp.DTOs.SaveNewClientResponse;
import com.noronsoft.noroncontrolapp.JWT.JwtUtil;
import com.noronsoft.noroncontrolapp.requestParams.SaveNewClientRequest;
import com.noronsoft.noroncontrolapp.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ClientController(ClientService clientService, JwtUtil jwtUtil) {
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/checkClient")
    public ResponseEntity<Map<String, String>> checkClient(@RequestParam String username, @RequestParam String password) {
        try {
            return clientService.checkClient(username, password).map(client -> {
                // Token üretimi
                String accessToken = jwtUtil.generateAccessToken(username);
                String refreshToken = jwtUtil.generateRefreshToken(username);
                System.out.println("CheckClient called with username: " + username + " and password: " + password);

                // Yalnızca token'ları döndür
                return ResponseEntity.ok(Map.of(
                        "access_token", accessToken,
                        "refresh_token", refreshToken
                ));
            }).orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing the request"));
        }
    }

    @PostMapping("/saveNewClient")
    public ResponseEntity<SaveNewClientResponse> saveNewClient(@RequestBody SaveNewClientRequest saveNewClientRequest, BindingResult result) {
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

    @GetMapping("/clientDetails")
    public ResponseEntity<?> getClientDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String token = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(token, true);

            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
            }

            return clientService.clientDetail(username)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Client not found")));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while processing the request"));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            System.out.println("Extracting username from refresh token...");
            String username = jwtUtil.extractUsername(refreshToken, false); // Refresh token olduğunu belirtiyoruz
            System.out.println("Validating refresh token...");
            if (jwtUtil.validateRefreshToken(refreshToken, username)) {
                System.out.println("Generating new access token...");
                String newAccessToken = jwtUtil.generateAccessToken(username);
                return ResponseEntity.ok(Map.of(
                        "access_token", newAccessToken,
                        "refresh_token", refreshToken)
                );
            } else {
                System.out.println("Invalid refresh token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the token");
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
