package com.noronsoft.noroncontrolapp.repositories;

import com.noronsoft.noroncontrolapp.models.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository  extends JpaRepository<ClientModel, Integer> {

    Optional<ClientModel> findByUsernameAndPassword(String username , String password);

    Optional<ClientModel> findByUsername(String username);
}
