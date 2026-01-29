package com.example.pvpeev.electronics_store.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserRequest {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String phoneNumber;
}
