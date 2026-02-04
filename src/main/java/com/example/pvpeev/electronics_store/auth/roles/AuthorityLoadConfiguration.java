package com.example.pvpeev.electronics_store.auth.roles;

import com.example.pvpeev.electronics_store.auth.mapper.AuthorityMapper;
import com.example.pvpeev.electronics_store.auth.repository.AuthorityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorityLoadConfiguration {

    @Bean
    public AuthorityResolver getAuthorityResolver(AuthorityRepository authorityRepository, AuthorityMapper mapper) {
        return new AuthorityResolverImpl(authorityRepository.findAll(), mapper);
    }
}
