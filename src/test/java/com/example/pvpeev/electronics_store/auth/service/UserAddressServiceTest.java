package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserAddressMapper;
import com.example.pvpeev.electronics_store.auth.mapper.UserAddressMapperImpl;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserAddressMapper userAddressMapper = new UserAddressMapperImpl();

    @InjectMocks
    private UserAddressService userAddressService;

    @Test
    public void testFindAllByUserIdWithUnexistingUser() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.findByIdAndEnabledIsTrue(userId)).thenReturn(Optional.empty());

        final RuntimeException exception = catchRuntimeException(() -> userAddressService.findAllByUserId(userId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(userId);
    }

    @Test
    public void testFindAllByUserId() {
        final UserEntity userEntity = new UserEntity(UUID.randomUUID(), "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true);
        final UserAddressEntity addressEntity = new UserAddressEntity(1L, userEntity.getId(), "Test address");

        when(userRepository.findByIdAndEnabledIsTrue(addressEntity.getUserId())).thenReturn(Optional.of(userEntity));
        when(userAddressRepository.findAllByUserId(addressEntity.getUserId())).thenReturn(List.of(addressEntity));

        final UserAddressResponse expectedResponse = new UserAddressResponse(addressEntity.getId(), addressEntity.getUserId(), addressEntity.getAddress());
        assertThat(userAddressService.findAllByUserId(addressEntity.getUserId()))
                .as("The service must return exactly one address")
                .singleElement()
                .as("The response doesn't match the expected response")
                .isEqualTo(expectedResponse);

        verify(userRepository, times(1)).findByIdAndEnabledIsTrue(addressEntity.getUserId());
        verify(userAddressRepository, times(1)).findAllByUserId(addressEntity.getUserId());
        verify(userAddressMapper, times(1)).toResponse(addressEntity);
    }

    @Test
    public void testCreateAddress() {
        final UUID userId = UUID.randomUUID();

        final UserAddressRequest addressRequest = new UserAddressRequest("Test address");
        final UserAddressEntity toSave = new UserAddressEntity(null, userId, addressRequest.getAddress());
        final UserAddressEntity savedEntity = new UserAddressEntity(1L, userId, addressRequest.getAddress());

        when(userAddressRepository.save(any(UserAddressEntity.class))).thenReturn(savedEntity);

        final UserAddressResponse expectedResponse = new UserAddressResponse(savedEntity.getId(), userId, addressRequest.getAddress());
        assertThat(userAddressService.createUserAddress(addressRequest, userId))
                .as("The response doesn't match the expected response")
                .isEqualTo(expectedResponse);

        verify(userAddressMapper, times(1)).toEntity(addressRequest, userId);
        verify(userAddressRepository, times(1)).save(toSave);
    }

    @Test
    public void testDeleteUnexistingAddressThrowsException() {
        final Long id = 1L;
        when(userAddressRepository.deleteByIdWithCount(id)).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> userAddressService.deleteById(id));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAddressRepository, times(1)).deleteByIdWithCount(id);
    }

    @Test
    public void testDeleteAddress() {
        final Long id = 1L;
        when(userAddressRepository.deleteByIdWithCount(id)).thenReturn(1);

        userAddressService.deleteById(id);

        verify(userAddressRepository, times(1)).deleteByIdWithCount(id);
    }
}
