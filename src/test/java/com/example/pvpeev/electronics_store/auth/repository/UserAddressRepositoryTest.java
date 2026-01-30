package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class UserAddressRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddingTheSameAddressTwiceThrowsException() {
        final UserEntity userEntity = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        final UUID userId = userRepository.save(userEntity).getId();

        userAddressRepository.save(new UserAddressEntity(null, userId, "Test street 1"));
        assertThrows(DataIntegrityViolationException.class,
                () -> userAddressRepository.save(new UserAddressEntity(null, userId, "Test street 1")),
                "The same address record can't be saved twice for the same user");
    }

    @Test
    public void testAddingTheSameAddressTwiceForDifferentUser() {
        final UserEntity userEntity1 = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        final UserEntity userEntity2 = new UserEntity(null, "igivanov@store.com", "Ivan", "Ivanov", "{noop}password", "+359897401214", true);
        final UUID userId1 = userRepository.save(userEntity1).getId();
        final UUID userId2 = userRepository.save(userEntity2).getId();

        final UserAddressEntity addr1 = userAddressRepository.save(new UserAddressEntity(null, userId1, "Test street 1"));
        final UserAddressEntity addr2 = userAddressRepository.save(new UserAddressEntity(null, userId2, "Test street 1"));

        final List<UserAddressEntity> allByUserId1 = userAddressRepository.findAllByUserId(userId1);
        assertEquals(1, allByUserId1.size(), "User 1 must have exactly one address");
        assertTrue(allByUserId1.stream().anyMatch(ae -> ae.getId().equals(addr1.getId())),
                String.format("User 1 must have address with id %s", addr1.getId()));

        final List<UserAddressEntity> allByUserId2 = userAddressRepository.findAllByUserId(userId2);
        assertEquals(1, allByUserId2.size(), "User 2 must have exactly one address");
        assertTrue(allByUserId2.stream().anyMatch(ae -> ae.getId().equals(addr2.getId())),
                String.format("User 2 must have address with id %s", addr2.getId()));

        assertEquals(1, userAddressRepository.deleteByUserId(userId1), "The query must delete exactly one address");
        assertEquals(1, userAddressRepository.deleteByIdWithCount(addr2.getId()), "The query must delete exactly one");
    }

    @Test
    public void testAddingTwoDifferentAddressesForTheSameUser() {
        final UserEntity userEntity = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        final UUID userId = userRepository.save(userEntity).getId();

        final String street1 = "Test street 1";
        final String street2 = "Test street 2";

        final UserAddressEntity uae1 = new UserAddressEntity(null, userId, street1);
        final UserAddressEntity uae2 = new UserAddressEntity(null, userId, street2);

        userAddressRepository.save(uae1);
        userAddressRepository.save(uae2);

        final List<UserAddressEntity> allByUserId = userAddressRepository.findAllByUserId(userId);
        assertEquals(2, allByUserId.size(), "The user must have exactly two address records");
        assertTrue(allByUserId.stream().anyMatch(uar -> street1.equals(uar.getAddress())),
                String.format("Address %s not found", street1));
        assertTrue(allByUserId.stream().anyMatch(uar -> street2.equals(uar.getAddress())),
                String.format("Address %s not found", street2));
    }
}
