package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapper;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.auth.roles.AuthorityResolver;
import com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp;
import com.example.pvpeev.electronics_store.auth.roles.TestAuthorityResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Spy
    private AuthorityResolver authorityResolver = new TestAuthorityResolver();

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        final UserRequest userRequest = new UserRequest("pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213");
        final UserEntity toSave = new UserEntity(null, userRequest.getEmail(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getPassword(), userRequest.getPhoneNumber(), true);
        final UserEntity savedEntity = new UserEntity(UUID.randomUUID(), userRequest.getEmail(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getPassword(), userRequest.getPhoneNumber(), true);

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        assertThat(userService.create(userRequest))
                .as("The id must match the id generated in the database")
                .isEqualTo(savedEntity.getId());

        verify(userMapper, times(1)).toEntity(userRequest, true);
        verify(userRepository, times(1)).save(toSave);
        verify(userAuthorityRepository, times(1))
                .save(new UserAuthorityEntity(null, savedEntity.getId(), authorityResolver.resolveIdByRoleConstant(RoleConstantsp.ROLE_STORE_USER)));
    }

    @Test
    public void testGetByIdReturnsOptionalEmptyOnUnexistingUser() {
        when(userRepository.findByIdAndEnabledIsTrue(any())).thenReturn(Optional.empty());

        assertThat(userService.getById(UUID.randomUUID()))
                .as("The service must return empty optional if the user doesn't exist")
                .isEmpty();

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(any());
    }

    @Test
    public void testGetByIdWhenUserExists() {
        final UserEntity userEntity = new UserEntity(UUID.randomUUID(), "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        when(userRepository.findByIdAndEnabledIsTrue(userEntity.getId())).thenReturn(Optional.of(userEntity));

        final UserResponse expectedResponse = new UserResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getPhoneNumber());
        assertThat(userService.getById(userEntity.getId()))
                .as("The service must return non empty optional")
                .isPresent()
                .get()
                .as("The response doesn't match the expected response")
                .isEqualTo(expectedResponse);

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(userEntity.getId());
        verify(userMapper, times(1)).toResponse(userEntity);
    }

    @Test
    public void testDeleteByIdUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.softDeleteUser(userId)).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> userService.deleteById(userId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        final InOrder inOrder = inOrder(userAddressRepository, userAuthorityRepository, userRepository);
        inOrder.verify(userAddressRepository, times(1)).deleteByUserId(userId);
        inOrder.verify(userAuthorityRepository, times(1)).deleteByUserId(userId);
        inOrder.verify(userRepository, times(1)).softDeleteUser(userId);
    }

    @Test
    public void testDeleteUserById() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.softDeleteUser(userId)).thenReturn(1);

        userService.deleteById(userId);

        final InOrder inOrder = inOrder(userAddressRepository, userAuthorityRepository, userRepository);
        inOrder.verify(userAddressRepository, times(1)).deleteByUserId(userId);
        inOrder.verify(userAuthorityRepository, times(1)).deleteByUserId(userId);
        inOrder.verify(userRepository, times(1)).softDeleteUser(userId);
    }
}
