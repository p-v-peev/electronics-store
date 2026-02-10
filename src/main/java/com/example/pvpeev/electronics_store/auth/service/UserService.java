package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.UserRequest;
import com.example.pvpeev.electronics_store.auth.dto.UserResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.mapper.UserMapper;
import com.example.pvpeev.electronics_store.auth.repository.UserAddressRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.auth.roles.AuthorityResolver;
import com.example.pvpeev.electronics_store.auth.roles.RoleConstantsp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserAddressRepository userAddressRepository;

    private final AuthorityResolver authorityResolver;

    @Transactional
    public UUID create(UserRequest request) {
        final UserEntity entity = userMapper.toEntity(request, true);
        final UUID id = userRepository.save(entity).getId();
        final Integer authorityId = authorityResolver.resolveIdByRoleConstant(RoleConstantsp.ROLE_STORE_USER);
        userAuthorityRepository.save(new UserAuthorityEntity(null, id, authorityId));
        return id;
    }

    public Optional<UserResponse> getById(UUID id) {
        return userRepository.findByIdAndEnabledIsTrue(id).map(userMapper::toResponse);
    }

    public void softDeleteById(UUID id) {
        userAddressRepository.deleteByUserId(id);
        userAuthorityRepository.deleteByUserId(id);
        final int result = userRepository.softDeleteUser(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
