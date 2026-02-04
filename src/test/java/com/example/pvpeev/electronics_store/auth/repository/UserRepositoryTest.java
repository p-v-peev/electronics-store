package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityEntityResolver;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp.ROLE_STORE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.assertj.core.api.InstanceOfAssertFactories.SET;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class UserRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Test
    public void testAddingTheSameUserTwice() {
        userRepository.save(getUserEntity());

        final RuntimeException exception = catchRuntimeException(() -> userRepository.save(getUserEntity()));
        assertThat(exception)
                .as("The same user can't be added twice.")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testFindUserAuthByEmail() {
        final AuthorityEntity authority = TestAuthorityEntityResolver.resolveByRoleConstant(ROLE_STORE_USER);
        final UserEntity userEntity = userRepository.save(getUserEntity());

        userAuthorityRepository.save(new UserAuthorityEntity(null, userEntity.getId(), authority.getId()));

        assertThat(userRepository.findUserAuthByEmail(userEntity.getEmail()))
                .as("The user must exist in the repository")
                .isPresent()
                .get()
                .extracting(UserAuthEntity::getAuthorities)
                .as("The authorities must be instance of Set<>() for constant time search")
                .asInstanceOf(SET)
                .as("The user must have exactly one authority")
                .singleElement()
                .isEqualTo(authority.getName());
    }

    @Test
    public void testSoftDeleteUser() {
        final UserEntity entityToSave = getUserEntity();
        final UserEntity user1 = userRepository.save(entityToSave);
        final UserEntity user2 = userRepository.save(new UserEntity(null, "igivanov@store.com", "Ivan", "Ivanov", "{noop}password", "+359897401214", true));

        assertThat(userRepository.softDeleteUser(user1.getId()))
                .as("Exactly one user must be deleted")
                .isEqualTo(1);
        assertThat(userRepository.findUserAuthByEmail(user2.getEmail()))
                .as("User 2 must not be deleted")
                .isPresent();
        assertThat(userRepository.findByIdAndEnabledIsTrue(user2.getId()))
                .as("User 2 must not be deleted")
                .isPresent();

        assertThat(userRepository.findUserAuthByEmail(user1.getEmail()))
                .as("The application must not find the user")
                .isEmpty();
        assertThat(userRepository.findByIdAndEnabledIsTrue(user1.getId()))
                .as("The application must not find the user")
                .isEmpty();

        final UserEntity expectedEntity = new UserEntity(user1.getId(), user1.getId().toString(), null, null, "{noop}" + user1.getId(), null, false);
        assertThat(userRepository.findById(user1.getId()))
                .as("The record must stay in the database")
                .isPresent()
                .get()
                .isEqualTo(expectedEntity);

        // Since the previous record is anonymised the application must be able to add the same user again
        assertThat(userRepository.save(getUserEntity()))
                .as("The same user must be able to register again")
                .satisfies(userEntity -> {
                    assertThat(userEntity.getId()).isNotNull();
                    assertThat(userEntity.getEmail()).isEqualTo(entityToSave.getEmail());
                    assertThat(userEntity.getFirstName()).isEqualTo(entityToSave.getFirstName());
                    assertThat(userEntity.getLastName()).isEqualTo(entityToSave.getLastName());
                    assertThat(userEntity.getPassword()).isEqualTo(entityToSave.getPassword());
                    assertThat(userEntity.getPhoneNumber()).isEqualTo(entityToSave.getPhoneNumber());
                    assertThat(userEntity.isEnabled()).isTrue();
                });
    }

    private static @NotNull UserEntity getUserEntity() {
        return new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
    }
}
