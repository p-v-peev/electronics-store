package com.example.pvpeev.electronics_store.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "store_user")
@RequiredArgsConstructor
@Getter
public class UserEntity {

    @Id
    private final UUID id;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String password;

    private final String phoneNumber;

    private final boolean enabled;
}
