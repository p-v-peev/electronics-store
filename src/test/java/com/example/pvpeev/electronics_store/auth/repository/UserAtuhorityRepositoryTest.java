package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class UserAtuhorityRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGrantingAuthorityToUnexistingUser() {
        final AuthorityEntity authority = authorityRepository.findAll().getFirst();

        assertThrows(DataIntegrityViolationException.class,
                () -> userAuthorityRepository.save(new UserAuthorityEntity(null, UUID.randomUUID(), authority.getId())),
                "Authorities can't be granted to unexisting users");
    }

    @Test
    public void testGrandAndDeleteAuthorities() {
        final List<AuthorityEntity> authorities = new ArrayList<>(authorityRepository.findAll());

        UserEntity user = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
        // There are four default authorities. Getting by index from 0 to 3 is safe.
        userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authorities.get(0).getId()));
        userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authorities.get(1).getId()));

        assertEquals(2, userAuthorityRepository.findAllByUserId(user.getId()).size(),
                "The user mst have exactly two authorities");
        assertEquals(1, userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), authorities.get(0).getId()),
                "Exactly one user authority must be deleted");
        // Deleting an authority the user doesn't have must do nothing
        assertEquals(0, userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), authorities.get(0).getId()),
                "Exactly zero user authorities must be deleted");
        assertEquals(1, userAuthorityRepository.deleteByUserId(user.getId()),
                "Exactly one user authority must be deleted");
        assertEquals(0, userAuthorityRepository.findAllByUserId(user.getId()).size(),
                "The user must not have anu authorities");
    }


}
