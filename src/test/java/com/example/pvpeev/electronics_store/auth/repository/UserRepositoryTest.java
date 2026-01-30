package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthEntity;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testAddingTheSameUserTwice() throws InterruptedException {
        userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true)),
                "The same user can't be added twice.");
    }

    @Test
    public void testGrantUserAuthority() {
        final AuthorityEntity authority = authorityRepository.findAll().getFirst();
        final UserEntity userEntity = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        userAuthorityRepository.save(new UserAuthorityEntity(null, userEntity.getId(), authority.getId()));

        final Optional<UserAuthEntity> userAuthByEmail = userRepository.findUserAuthByEmail(userEntity.getEmail());
        assertTrue(userAuthByEmail.isPresent(), "The user must exist in the repository");
        final UserAuthEntity userAuthEntity = userAuthByEmail.get();
        assertEquals(1, userAuthEntity.getAuthorities().size(), "The user must have exactly one authority");
        assertTrue(userAuthEntity.getAuthorities().contains(authority.getName()), String.format("The authorities must contain %s", authority.getName()));

        assertThrows(DataIntegrityViolationException.class,
                () -> userAuthorityRepository.save(new UserAuthorityEntity(null, userEntity.getId(), authority.getId())),
                "The same user can't have the same authority twice.");
    }

    @Test
    public void testSoftDeleteUser() {
        final UserEntity userEntity = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        userRepository.softDeleteUser(userEntity.getId());
        assertTrue(userRepository.findUserAuthByEmail(userEntity.getEmail()).isEmpty(), "The application must not find the user");
        assertTrue(userRepository.findByIdAndEnabledIsTrue(userEntity.getId()).isEmpty(), "The application must not find the user");

        final Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        assertFalse(userOptional.isEmpty(), "The record must stay in the database");

        final UserEntity user = userOptional.get();
        assertEquals(userEntity.getId(), user.getId(), "The id must not change to keep the DB relations valid");
        assertEquals(userEntity.getId().toString(), user.getEmail(), "The email must be deleted as per GDPR");
        assertEquals("{noop}" + userEntity.getId().toString(), user.getPassword(), "The password must be changed to something really hard to guess");
        assertNull(user.getFirstName(), "The first name must be null as per GDPR");
        assertNull(user.getLastName(), "The last name must be null as per GDPR");
        assertNull(user.getPhoneNumber(), "The phone number must be null as per GDPR");
        assertFalse(user.isEnabled(), "The account must be disabled to prevent further use");

        // Since the previous record is anonymised the application must be able to add the same user again
        userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
    }
}
