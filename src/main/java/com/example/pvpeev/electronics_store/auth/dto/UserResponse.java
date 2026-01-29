package com.example.pvpeev.electronics_store.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UserResponse {
    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
}
