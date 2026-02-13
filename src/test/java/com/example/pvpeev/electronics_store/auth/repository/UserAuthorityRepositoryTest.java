package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_USER;
import static com.example.pvpeev.electronics_store.auth.authorities.Authority.ROLE_STORE_WAREHOUSE_WORKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class UserAuthorityRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGrantAuthorityToUnexistingUser() {
        final RuntimeException exception = catchRuntimeException(() -> userAuthorityRepository.save(new UserAuthorityEntity(null, UUID.randomUUID(), ROLE_STORE_USER.getId())));
        assertThat(exception)
                .as("Authorities can't be granted to unexisting users")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testGrantTheSameAuthorityTwiceThrowsException() {

        final UserEntity user = userRepository.save(getUserEntity());
        userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), ROLE_STORE_USER.getId()));

        final RuntimeException exception = catchRuntimeException(() -> userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), ROLE_STORE_USER.getId())));
        assertThat(exception)
                .as("The same authority can't be granted twice to the same user")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testGrantAndDeleteAuthorities() {
        final UserEntity user = userRepository.save(getUserEntity());
        // There are four default authorities. Getting by index from 0 to 3 is safe.
        final UserAuthorityEntity authority1 = userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), ROLE_STORE_USER.getId()));
        final UserAuthorityEntity authority2 = userAuthorityRepository.save(new UserAuthorityEntity(null, user.getId(), ROLE_STORE_WAREHOUSE_WORKER.getId()));

        assertThat(userAuthorityRepository.findAllByUserId(user.getId()))
                .as("The user mst have exactly two authorities")
                .hasSize(2)
                .as("The user must have exactly the authorities saved above")
                .containsExactlyInAnyOrder(authority1, authority2);
        assertThat(userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), ROLE_STORE_USER.getId()))
                .as("Exactly one user authority must be deleted")
                .isEqualTo(1);
        // Deleting the previously deleted authority must do nothing
        assertThat(userAuthorityRepository.deleteByUserIdAndAuthorityId(user.getId(), ROLE_STORE_USER.getId()))
                .as("Exactly zero user authorities must be deleted")
                .isEqualTo(0);
        assertThat(userAuthorityRepository.deleteByUserId(user.getId()))
                .as("Exactly one user authority must be deleted")
                .isEqualTo(1);
        assertThat(userAuthorityRepository.findAllByUserId(user.getId()))
                .as("The user must not have any authorities")
                .isEmpty();
    }

    private static @NotNull UserEntity getUserEntity() {
        return new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
    }


}
