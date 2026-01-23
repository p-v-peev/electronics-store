package com.example.pvpeev.electronics_store.auth.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_authority")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserAuthorityEntity {

    @EmbeddedId
    private UserAuthorityId id;


    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserAuthorityId implements Serializable {
        private UUID userId;
        private UUID user_authority_id;
    }
}
