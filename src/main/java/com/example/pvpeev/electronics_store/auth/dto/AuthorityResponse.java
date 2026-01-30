package com.example.pvpeev.electronics_store.auth.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class AuthorityResponse {
    private final Integer id;
    private final String name;
    private final String description;
}
