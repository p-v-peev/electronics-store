package com.example.pvpeev.electronics_store.auth.service;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityMapper authorityMapper;

    private final AuthorityRepository authorityRepository;

    public List<AuthorityResponse> findAll() {
        return authorityRepository.findAll().stream().map(authorityMapper::toResponse).toList();
    }
}
