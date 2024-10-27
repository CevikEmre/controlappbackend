package com.noronsoft.noroncontrolapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class ClientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @NotNull
    private String name;

    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    private String password;

    @Column(name = "createdDateTime", columnDefinition = "DATETIME")
    private LocalDateTime createdDateTime;

    @NotNull
    private String address;

    @NotNull
    private String city;

    @NotNull
    private String country;

    @Column(unique = true)
    @Email(message = "Invalid email format")
    @NotNull
    private String email;

    @Column(unique = true)
    @NotNull
    private String phone;

    @Column(columnDefinition = "TINYINT(1)")
    @NotNull
    private Boolean enable;

    @Column(unique = true)
    @NotNull
    private String deviceToken;

    @ManyToMany(mappedBy = "otherClients")
    private Set<DeviceModel> devices = new HashSet<>();
}
