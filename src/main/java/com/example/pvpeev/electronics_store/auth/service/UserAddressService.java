package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserAddressResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserAddressMapper;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressMapper userAddressMapper;

    private final UserAddressRepository userAddressRepository;

    private final UserRepository userRepository;

    public List<UserAddressResponse> findAllByUserId(UUID userId) {
        final boolean exists = userRepository.findByIdAndEnabledIsTrue(userId).isPresent();
        if (!exists) {
            throw new ResourceNotFoundException();
        }
        return userAddressRepository.findAllByUserId(userId).stream().map(userAddressMapper::toResponse).toList();
    }

    public UserAddressResponse createUserAddress(UserAddressRequest request, UUID userId) {
        final UserAddressEntity entity = userAddressRepository.save(userAddressMapper.toEntity(request, userId));
        return userAddressMapper.toResponse(entity);
    }

    public void deleteById(Long id) {
        int result = userAddressRepository.deleteByIdWithCount(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
