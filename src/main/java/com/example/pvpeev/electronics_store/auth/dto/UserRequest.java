package com.example.pvpeev.electronics_store.auth.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class UserRequest {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String phoneNumber;
}
