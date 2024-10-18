package com.noronsoft.noroncontrolapp.repositories;

import com.noronsoft.noroncontrolapp.models.IoCommandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IORepository  extends JpaRepository<IoCommandModel ,Integer> {
    Optional<IoCommandModel> findLastestByDevId(Integer devId);
}
