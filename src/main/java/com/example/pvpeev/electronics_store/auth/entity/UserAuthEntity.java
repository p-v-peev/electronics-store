package com.example.pvpeev.electronics_store.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table(name = "store_user")
@RequiredArgsConstructor
@Getter
public class UserAuthEntity {

    @Id
    private final UUID id;

    private final String email;

    private final String password;

    private final boolean accountExpired;

    private final boolean accountLocked;

    private final boolean credentialsExpired;

    private final boolean enabled;

    private final Set<String> authorities;
}
