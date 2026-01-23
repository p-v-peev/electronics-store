package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
        final UserEntity userEntity = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", false, false, false, false);
        final UUID userId = userRepository.save(userEntity).getId();

        userAddressRepository.save(new UserAddressEntity(null, userId, "Test street 1", "032"));
        assertThrows(Throwable.class,
                () -> userAddressRepository.save(new UserAddressEntity(null, userId, "Test street 1", "032")),
                "The same address record can't be saved twice");
    }

    @Test
    public void testAddingTwoDifferentAddresses() {
        final UserEntity userEntity = new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", false, false, false, false);
        final UUID userId = userRepository.save(userEntity).getId();

        final String street1 = "Test street 1";
        final String street2 = "Test street 2";

        final UserAddressEntity uae1 = new UserAddressEntity(null, userId, street1, "032");
        final UserAddressEntity uae2 = new UserAddressEntity(null, userId, street2, "032");

        userAddressRepository.save(uae1);
        userAddressRepository.save(uae2);

        final List<UserAddressEntity> allByUserId = userAddressRepository.findAllByUserId(userId);
        assertEquals(2, allByUserId.size(), "The user must have only two address records");
        assertTrue(allByUserId.stream().anyMatch(uar -> street1.equals(uar.getAddress())),
                String.format("Address %s not found", street1));
        assertTrue(allByUserId.stream().anyMatch(uar -> street2.equals(uar.getAddress())),
                String.format("Address %s not found", street2));
    }
}
