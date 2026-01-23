package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class AuthorityRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    public void testSystemAuthoritiesExist() {
        final HashSet<AuthorityEntity> authorities = new HashSet<>(authorityRepository.findAll());
        assertTrue(authorities.stream().anyMatch(ae -> ae.getName().equals(ROLE_STORE_USER.name())),
                String.format("The default %s roles is not in the database", ROLE_STORE_USER.name())
        );

        assertTrue(authorities.stream().anyMatch(ae -> ae.getName().equals(ROLE_STORE_WAREHOUSE_WORKER.name())),
                String.format("The default %s roles is not in the database", ROLE_STORE_WAREHOUSE_WORKER.name())
        );
        assertTrue(authorities.stream().anyMatch(ae -> ae.getName().equals(ROLE_STORE_PRODUCT_ADMIN.name())),
                String.format("The default %s roles is not in the database", ROLE_STORE_PRODUCT_ADMIN.name())
        );
        assertTrue(authorities.stream().anyMatch(ae -> ae.getName().equals(ROLE_STORE_AUTHORITY_ADMIN.name())),
                String.format("The default %s roles is not in the database", ROLE_STORE_AUTHORITY_ADMIN.name())
        );
    }

}
