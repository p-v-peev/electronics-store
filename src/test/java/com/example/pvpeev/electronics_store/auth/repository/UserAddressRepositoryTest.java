package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;

public class UserAddressRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddTheSameAddressTwiceThrowsException() {
        final UserEntity user1 = userRepository.save(getUserEntity());

        userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));

        final RuntimeException throwable = catchRuntimeException(() -> userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1")));
        assertThat(throwable)
                .as("The same address record can't be saved twice for the same user")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testAddTheSameAddressForDifferentUsers() {
        final UserEntity user1 = userRepository.save(getUserEntity());
        final UserEntity user2 = userRepository.save(new UserEntity(null, "igivanov@store.com", "Ivan", "Ivanov", "{noop}password", "+359897401214", true));

        final UserAddressEntity address1 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));
        final UserAddressEntity address2 = userAddressRepository.save(new UserAddressEntity(null, user2.getId(), "Test street 1"));

        assertThatList(userAddressRepository.findAllByUserId(user1.getId()))
                .as("User 1 must have exactly one address")
                .singleElement()
                .as("User 1 must have the address saved above")
                .isEqualTo(address1);

        assertThatList(userAddressRepository.findAllByUserId(user2.getId()))
                .as("User 2 must have exactly one address")
                .singleElement()
                .as("User 2 must have the address saved above")
                .isEqualTo(address2);

        assertThat(userAddressRepository.deleteByUserId(user1.getId()))
                .as("The query must delete exactly one address")
                .isEqualTo(1);

        assertThat(userAddressRepository.deleteByIdWithCount(address2.getId()))
                .as("The query must delete exactly one address")
                .isEqualTo(1);
    }

    @Test
    public void testAddTwoDifferentAddressesForTheSameUser() {
        final UserEntity user1 = userRepository.save(getUserEntity());

        final UserAddressEntity address1 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 1"));
        final UserAddressEntity address2 = userAddressRepository.save(new UserAddressEntity(null, user1.getId(), "Test street 2"));

        assertThatList(userAddressRepository.findAllByUserId(user1.getId()))
                .as("The user must have exactly two addresses")
                .hasSize(2)
                .as("The addresses must be exactly the ones saved above")
                .containsExactlyInAnyOrder(address1, address2);
    }

    private static @NotNull UserEntity getUserEntity() {
        return new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
    }
}
