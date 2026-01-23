package com.example.pvpeev.electronics_store.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "user_authority")
@RequiredArgsConstructor
@Getter
public class UserAuthorityEntity {

    @Id
    private final UUID id;

    private final UUID userId;

    private final UUID authorityId;
}
