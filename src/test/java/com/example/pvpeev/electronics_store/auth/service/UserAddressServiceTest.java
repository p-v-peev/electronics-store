package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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

        when(userRepository.existsById(eq(userId))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userAddressService.findAllByUserId(userId),
                "Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404");

        verify(userRepository, times(1)).existsById(eq(userId));
    }

    @Test
    public void testFindAllByUserId() {
        final Long addressId = 1L;
        final UUID userId = UUID.randomUUID();
        final String address = "Troyan, Bulgaria";
        final UserAddressEntity entity = new UserAddressEntity(addressId, userId, address);

        when(userRepository.existsById(eq(userId))).thenReturn(true);
        when(userAddressRepository.findAllByUserId(eq(userId))).thenReturn(List.of(entity));

        final List<UserAddressResponse> responses = userAddressService.findAllByUserId(userId);
        assertEquals(1, responses.size(), "The service must return exactly one address");
        final UserAddressResponse response = responses.getFirst();
        final UserAddressResponse expectedResponse = new UserAddressResponse(addressId, userId, address);
        assertEquals(expectedResponse, response, "The response doesn't match the expected response");

        verify(userRepository, times(1)).existsById(eq(userId));
        verify(userAddressRepository, times(1)).findAllByUserId(eq(userId));
        verify(userAddressMapper, times(1)).toResponse(eq(entity));
    }

    @Test
    public void testCreateAddress() {
        final Long addressId = 1L;
        final UUID userId = UUID.randomUUID();
        final String address = "Troyan, Bulgaria";

        // Return the
        when(userAddressRepository.save(any())).then(invocation -> {
            final UserAddressEntity argument = invocation.getArgument(0, UserAddressEntity.class);
            return new UserAddressEntity(addressId, argument.getUserId(), argument.getAddress());
        });

        final UserAddressResponse response = userAddressService.createUserAddress(new UserAddressRequest(address), userId);
        final UserAddressResponse expectedResponse = new UserAddressResponse(addressId, userId, address);
        assertEquals(expectedResponse, response, "The response doesn't match the expected response");
    }

    @Test
    public void testDeleteUnexistingAddressThrowsException() {
        when(userAddressRepository.deleteByIdWithCount(eq(1L))).thenReturn(0);

        assertThrows(ResourceNotFoundException.class,
                () -> userAddressService.deleteById(1L),
                "Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404");

        verify(userAddressRepository, times(1)).deleteByIdWithCount(eq(1L));
    }

    @Test
    public void testDeleteAddress() {
        when(userAddressRepository.deleteByIdWithCount(eq(1L))).thenReturn(1);

        userAddressService.deleteById(1L);

        verify(userAddressRepository, times(1)).deleteByIdWithCount(eq(1L));
    }
}
