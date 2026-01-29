package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.AuthorityEntity;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthorityService {

    private final AuthorityMapper authorityMapper;

    private final UserAuthorityRepository userAuthorityRepository;

    private final AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    public List<AuthorityResponse> findAllByUserId(UUID userId) {
        final boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new ResourceNotFoundException();
        }
        final List<Integer> list = userAuthorityRepository.findAllByUserId(userId).stream().map(UserAuthorityEntity::getAuthorityId).toList();
        return authorityRepository.findAllById(list).stream().map(authorityMapper::toResponse).toList();
    }

    public AuthorityResponse createUserAuthority(UUID userId, Integer authorityId) {
        final UserAuthorityEntity userAuthority = new UserAuthorityEntity(null, userId, authorityId);
        userAuthorityRepository.save(userAuthority);
        // Must always have result because of the relation in the database
        final Optional<AuthorityEntity> authorityEntityOptional = authorityRepository.findById(authorityId);
        final AuthorityEntity authorityEntity = authorityEntityOptional.get();
        return authorityMapper.toResponse(authorityEntity);
    }

    public void revokeAuthority(UUID userId, Integer authorityId) {
        final int i = userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, authorityId);
        if (i == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
