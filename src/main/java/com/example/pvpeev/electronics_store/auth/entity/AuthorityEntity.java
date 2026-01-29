package com.example.pvpeev.electronics_store.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "authority")
@RequiredArgsConstructor
@Getter
public class AuthorityEntity {

    @Id
    private final Integer id;

    private final String name;

    private final String description;
}
