package com.example.pvpeev.electronics_store.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserAddressRequest {
    private final String address;
    private final String zipCode;
}
