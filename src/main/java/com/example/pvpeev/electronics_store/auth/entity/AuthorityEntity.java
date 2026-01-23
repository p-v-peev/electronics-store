package com.example.pvpeev.electronics_store.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "authority")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthorityEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;
}
