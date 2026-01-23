package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class UserRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Test
    public void testTest() throws InterruptedException {
        final AuthorityEntity authority = authorityRepository.findAll().getFirst();


        final UserEntity userEntity = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", false, false, false, false);
        final UUID userId = userRepository.save(userEntity).getId();

        final UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity(null, userId, authority.getId());
        userAuthorityRepository.save(userAuthorityEntity);

        final Optional<UserAuthEntity> userAuthByEmail = userRepository.findUserAuthByEmail(userEntity.getEmail());

        assertTrue(userAuthByEmail.isPresent(), "The user must exist in the repository");
        final UserAuthEntity userAuthEntity = userAuthByEmail.get();
        assertEquals(1, userAuthEntity.getAuthorities().size(), "The user must have one authority");
        assertTrue(userAuthEntity.getAuthorities().contains(authority.getName()),
                String.format("The authorities must contains %s", authority.getName()));
    }
}
