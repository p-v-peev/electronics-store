package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapper;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.roles.RoleConstants.ROLE_STORE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final AuthorityRepository authorityRepository;

    private final UserAddressRepository userAddressRepository;

    private volatile int roleStoreUserId;

    @PostConstruct
    public void init() {
        this.roleStoreUserId = authorityRepository.findByName(ROLE_STORE_USER.getValue())
                .map(AuthorityEntity::getId)
                .orElseThrow(() -> new RuntimeException(String.format("Role %s not found. Invalid application state.", ROLE_STORE_USER.getValue())));
    }

    @Transactional
    public String create(UserRequest request) {
        final UserEntity entity = userMapper.toEntity(request, true);
        final UUID id = userRepository.save(entity).getId();
        userAuthorityRepository.save(new UserAuthorityEntity(null, id, roleStoreUserId));
        return id.toString();
    }

    public Optional<UserResponse> getById(UUID id) {
        return userRepository.findByIdAndEnabledIsTrue(id).map(userMapper::toResponse);
    }

    public void deleteById(UUID id) {
        userAddressRepository.deleteByUserId(id);
        userAuthorityRepository.deleteByUserId(id);
        final int result = userRepository.softDeleteUser(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
