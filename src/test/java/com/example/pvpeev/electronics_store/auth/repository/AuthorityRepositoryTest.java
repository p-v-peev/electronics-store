package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityEntityResolver;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class AuthorityRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private AuthorityRepository authorityRepository;


    @Test
    public void testTheDefaultAuthoritiesExist() {
        assertThatList(authorityRepository.findAll())
                .as("All 4 authorities must be populated in the database.")
                .hasSize(4)
                .as("The following authorities must exist %s, %s, %s, %s",
                        ROLE_STORE_USER.getAuthority(),
                        ROLE_STORE_WAREHOUSE_WORKER.getAuthority(),
                        ROLE_STORE_PRODUCT_ADMIN.getAuthority(),
                        ROLE_STORE_AUTHORITY_ADMIN.getAuthority()
                )
                .containsExactlyInAnyOrder(
                        TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER),
                        TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_WAREHOUSE_WORKER),
                        TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_PRODUCT_ADMIN),
                        TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_AUTHORITY_ADMIN)
                );
    }

    @Test
    public void testFindByName() {
        assertThat(authorityRepository.findByName(ROLE_STORE_USER.getAuthority()))
                .isNotEmpty()
                .as("The repository must find the entity")
                .get()
                .as("The result must be equal to the expected authority entity")
                .isEqualTo(TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER));
    }

}
