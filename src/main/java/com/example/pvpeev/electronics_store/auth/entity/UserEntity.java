package com.example.pvpeev.electronics_store.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;

    private String firstName;

    private String lastName;

    private String password;

    private String phoneNumber;

    private boolean accountExpired;

    private boolean accountLocked;

    private boolean credentialsExpired;

    private boolean enabled;
}
