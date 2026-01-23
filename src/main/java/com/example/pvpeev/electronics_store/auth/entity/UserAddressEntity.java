package com.example.pvpeev.electronics_store.auth.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_address")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserAddressEntity {

    @EmbeddedId
    private UserAddressId id;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAddressId implements Serializable {
        private UUID userId;
        private String address;
        private String zipCode;
    }
}
