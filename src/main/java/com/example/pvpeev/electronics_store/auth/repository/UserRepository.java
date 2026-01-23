package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAuthEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    @Query("""
            SELECT su.id, su.email, su.password, su.account_expired, su.account_locked, su.credentials_expired, su.enabled, STRING_AGG(a.name, ',') AS authorities
            FROM store_user su
            JOIN user_authority ua ON su.id = ua.user_id
            JOIN authority a ON ua.authority_id = a.id
            WHERE su.email = :email GROUP BY su.id
            """)
    Optional<UserAuthEntity> findUserAuthByEmail(String email);
}
