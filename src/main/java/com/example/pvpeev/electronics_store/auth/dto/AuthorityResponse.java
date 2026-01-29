package com.example.pvpeev.electronics_store.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthorityResponse {
    private final Integer id;
    private final String name;
    private final String description;
}
