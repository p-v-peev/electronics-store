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
    public void testAddTheSameAddressTwiceThrowsException() {
        final UserEntity user1 = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));
        assertThrows(DataIntegrityViolationException.class,
                () -> userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1")),
                "The same address record can't be saved twice for the same user");
    }

    @Test
    public void testAddTheSameAddressForDifferentUsers() {
        final UserEntity user1 = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
        final UserEntity user2 = userRepository.save(new UserEntity(null, "igivanov@store.com", "Ivan", "Ivanov", "{noop}password", "+359897401214", true));

        final UserAddressEntity address1 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));
        final UserAddressEntity address2 = userAddressRepository.save(new UserAddressEntity(null, user2.getId(), "Test street 1"));

        final List<UserAddressEntity> allByUserId1 = userAddressRepository.findAllByUserId(user1.getId());
        assertEquals(1, allByUserId1.size(), "User 1 must have exactly one address");
        assertTrue(allByUserId1.stream().anyMatch(address1::equals), "User 1 must have the address saved above");

        final List<UserAddressEntity> allByUserId2 = userAddressRepository.findAllByUserId(user2.getId());
        assertEquals(1, allByUserId2.size(), "User 2 must have exactly one address");
        assertTrue(allByUserId2.stream().anyMatch(address2::equals), "User 2 must have address with id %s");

        assertEquals(1, userAddressRepository.deleteByUserId(user1.getId()), "The query must delete exactly one address");
        assertEquals(1, userAddressRepository.deleteByIdWithCount(address2.getId()), "The query must delete exactly one");
    }

    @Test
    public void testAddTwoDifferentAddressesForTheSameUser() {
        final UserEntity user1 = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));

        final UserAddressEntity address1 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));
        final UserAddressEntity address2 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 2"));

        final List<UserAddressEntity> allByUserId = userAddressRepository.findAllByUserId(user1.getId());
        assertEquals(2, allByUserId.size(), "The user must have exactly two address records");
        assertTrue(allByUserId.stream().anyMatch(address1::equals), "The user must have the first address saved above");
        assertTrue(allByUserId.stream().anyMatch(address2::equals), "The user must have the first address saved above");
    }
}
