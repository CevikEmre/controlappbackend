package com.noronsoft.noroncontrolapp.repositories;

import com.noronsoft.noroncontrolapp.models.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DeviceRepository  extends JpaRepository<DeviceModel , Integer> {
    Optional<DeviceModel> findByDevId(Integer devId);
    List<DeviceModel> findByClientId(Integer clientId);
}
