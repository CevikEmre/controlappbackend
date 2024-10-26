package com.noronsoft.noroncontrolapp.services;

import com.noronsoft.noroncontrolapp.models.ClientModel;
import com.noronsoft.noroncontrolapp.repositories.ClientRepository;
import com.noronsoft.noroncontrolapp.requestParams.SaveNewClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<ClientModel> checkClient(String username, String password) {
        System.out.println("Searching for user: " + username + " with password: " + password);
        return clientRepository.findByUsernameAndPassword(username, password);
    }
    public Optional<?> clientDetail(String username) {
        return clientRepository.findByUsername(username);
    }

    public Optional<ClientModel> findByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    public void saveClient(SaveNewClientRequest saveNewClientRequest) {
        ClientModel client = new ClientModel();
        client.setUsername(saveNewClientRequest.getUsername());
        client.setPassword(saveNewClientRequest.getPassword());
        client.setName(saveNewClientRequest.getName());
        client.setAddress(saveNewClientRequest.getAddress());
        client.setCity(saveNewClientRequest.getCity());
        client.setCountry(saveNewClientRequest.getCountry());
        client.setEmail(saveNewClientRequest.getEmail());
        client.setPhone(saveNewClientRequest.getPhone());
        client.setEnable(saveNewClientRequest.getEnable());
        client.setDeviceToken(saveNewClientRequest.getDeviceToken());
        client.setCreatedDateTime(LocalDateTime.now());

        clientRepository.save(client);
    }
}
