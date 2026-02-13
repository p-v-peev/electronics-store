package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.auth.authorities.Authority;
import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityMapper authorityMapper;

    public List<AuthorityResponse> findAll() {
        return Arrays.stream(Authority.values()).map(authorityMapper::toResponse).toList();
    }
}
