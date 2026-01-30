package com.example.pvpeev.electronics_store.auth.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class UserAddressResponse {
    private final Long id;
    private final UUID userId;
    private final String address;
}
