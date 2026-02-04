package com.example.pvpeev.electronics_store.auth.roles;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestAuthorityResolverConfiguration {

    @Bean
    @Primary
    public AuthorityResolver testAuthorityResolver() {
        return new TestAuthorityResolver();
    }
}
