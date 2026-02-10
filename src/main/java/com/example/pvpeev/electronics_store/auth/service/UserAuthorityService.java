package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.auth.authorities.Authorities;
import com.example.pvpeev.electronics_store.auth.authorities.AuthoritiesResolver;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.repository.UserAuthorityRepository;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthorityService {

    private final AuthorityMapper authorityMapper;

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserRepository userRepository;

    private final AuthoritiesResolver authoritiesResolver;

    public List<AuthorityResponse> findAllByUserId(UUID userId) {
        final boolean exists = userRepository.findByIdAndEnabledIsTrue(userId).isPresent();
        if (!exists) {
            throw new ResourceNotFoundException();
        }
        return userAuthorityRepository.findAllByUserId(userId)
                .stream()
                .map(UserAuthorityEntity::getAuthorityId)
                .map(authoritiesResolver::resolveById)
                .map(authorityMapper::toResponse)
                .toList();
    }

    public AuthorityResponse grantUserAuthority(UUID userId, Integer authorityId) {
        final Authorities authorities = authoritiesResolver.resolveById(authorityId);
        if (authorities == null) {
            throw new BadRequestException();
        }

        final UserAuthorityEntity userAuthority = new UserAuthorityEntity(null, userId, authorityId);
        userAuthorityRepository.save(userAuthority);

        return authorityMapper.toResponse(authorities);
    }

    public void revokeUserAuthority(UUID userId, Integer authorityId) {
        final Authorities authorities = authoritiesResolver.resolveById(authorityId);
        if (Authorities.ROLE_STORE_USER == authorities || authorities == null) {
            throw new BadRequestException();
        }

        final int i = userAuthorityRepository.deleteByUserIdAndAuthorityId(userId, authorityId);

        if (i == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
