package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapper;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        final UserRequest request = new UserRequest("pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213");
        final UserEntity expectedSave = new UserEntity(null, request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword(), request.getPhoneNumber(), true);
        final UserEntity resultSave = new UserEntity(UUID.randomUUID(), request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword(), request.getPhoneNumber(), true);

        when(userRepository.save(eq(expectedSave))).thenReturn(resultSave);

        final UUID id = userService.create(request);
        assertEquals(resultSave.getId(), id, "The id must match the id generated in the database");

        verify(userMapper, times(1)).toEntity(eq(request), eq(true));
        verify(userRepository, times(1)).save(eq(expectedSave));
        verify(userAuthorityRepository, times(1)).save(eq(new UserAuthorityEntity(null, resultSave.getId(), 0)));
    }

    @Test
    public void testGetByIdReturnsOptionalEmptyOnUnexistingUser() {
        when(userRepository.findByIdAndEnabledIsTrue(any())).thenReturn(Optional.empty());

        final Optional<UserResponse> response = userService.getById(UUID.randomUUID());
        assertTrue(response.isEmpty(), "The service must return empty optional if the user doesn't exist");

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(any());
    }

    @Test
    public void testGetByIdReturnsUser() {
        final UserEntity userEntity = new UserEntity(UUID.randomUUID(), "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        when(userRepository.findByIdAndEnabledIsTrue(any())).thenReturn(Optional.of(userEntity));

        final Optional<UserResponse> responseOptional = userService.getById(UUID.randomUUID());
        assertFalse(responseOptional.isEmpty(), "The service must return empty optional if the user doesn't exist");
        final UserResponse response = responseOptional.get();
        final UserResponse expectedResponse = new UserResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getPhoneNumber());
        assertEquals(expectedResponse, response, "The response doesn't match the expected response");

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(any());
        verify(userMapper, times(1)).toResponse(eq(userEntity));
    }

    @Test
    public void testDeleteByIdUnexistingUserThrowsException() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.softDeleteUser(eq(userId))).thenReturn(0);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteById(userId),
                "Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404");

        verify(userAddressRepository, times(1)).deleteByUserId(eq(userId));
        verify(userAuthorityRepository, times(1)).deleteByUserId(eq(userId));
        verify(userRepository, times(1)).softDeleteUser(eq(userId));
    }

    @Test
    public void testDeleteUserById() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.softDeleteUser(eq(userId))).thenReturn(1);

        userService.deleteById(userId);

        verify(userAddressRepository, times(1)).deleteByUserId(eq(userId));
        verify(userAuthorityRepository, times(1)).deleteByUserId(eq(userId));
        verify(userRepository, times(1)).softDeleteUser(eq(userId));
    }
}
