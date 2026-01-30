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

import static org.junit.jupiter.api.Assertions.*;

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
    public void testGrantAuthorityToUnexistingUser() {
        final AuthorityEntity authority = authorityRepository.findAll().getFirst();

        assertThrows(DataIntegrityViolationException.class,
                () -> userAuthorityRepository.save(new UserAuthorityEntity(null, UUID.randomUUID(), authority.getId())),
                "Authorities can't be granted to unexisting users");
    }

    @Test
    public void testGrantTheSameAuthorityTwiceThrowsException() {
        final AuthorityEntity authority = authorityRepository.findAll().getFirst();
        final UserEntity user = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authority.getId()));
        assertThrows(DataIntegrityViolationException.class,
                () -> userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authority.getId())),
                "The same authority can't be granted twice to the same user");
    }

    @Test
    public void testGrantAndDeleteAuthorities() {
        final List<AuthorityEntity> authorities = new ArrayList<>(authorityRepository.findAll());

        final UserEntity user = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
        // There are four default authorities. Getting by index from 0 to 3 is safe.
        final UserAuthorityEntity authority1 = userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authorities.get(0).getId()));
        final UserAuthorityEntity authority2 = userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), authorities.get(1).getId()));

        final List<UserAuthorityEntity> allByUserId = userAuthorityRepository.findAllByUserId(user.getId());
        assertEquals(2, allByUserId.size(), "The user mst have exactly two authorities");
        assertTrue(allByUserId.stream().anyMatch(authority1::equals), "The user must have the first authority saved above");
        assertTrue(allByUserId.stream().anyMatch(authority2::equals), "The user must have the second authority saved above");

        assertEquals(1, userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), authorities.getFirst().getId()),
                "Exactly one user authority must be deleted");
        // Deleting the previously deleted authority must do nothing
        assertEquals(0, userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), authorities.getFirst().getId()),
                "Exactly zero user authorities must be deleted");
        assertEquals(1, userAuthorityRepository.deleteByUserId(user.getId()),
                "Exactly one user authority must be deleted");
        assertEquals(0, userAuthorityRepository.findAllByUserId(user.getId()).size(),
                "The user must not have any authorities");
    }


}
